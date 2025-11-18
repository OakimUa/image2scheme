package org.oakim.img2scheme.image

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorFilter
import org.jetbrains.skia.ColorMatrix
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface
import org.oakim.img2scheme.Constants.COLOR_DEFAULT
import kotlin.math.pow
import kotlin.math.sqrt

fun adjustImageBrightness(bitmap: ImageBitmap?, brightness: Float): ImageBitmap? {
    if (bitmap == null) return null
    val surface = Surface.makeRasterN32Premul(bitmap.width, bitmap.height)
    val canvas = surface.canvas
    val paint = Paint().apply {
        colorFilter = ColorFilter.makeMatrix(
            ColorMatrix(
                1f, 0f, 0f, 0f, brightness,
                0f, 1f, 0f, 0f, brightness,
                0f, 0f, 1f, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
    canvas.drawImage(Image.makeFromBitmap(bitmap.asSkiaBitmap()), 0f, 0f, paint)
    return surface.makeImageSnapshot().toComposeImageBitmap()
}

fun adjustImageSaturation(bitmap: ImageBitmap?, saturation: Float): ImageBitmap? {
    if (bitmap == null) return null
    val surface = Surface.makeRasterN32Premul(bitmap.width, bitmap.height)
    val canvas = surface.canvas
    val paint = Paint().apply {
        colorFilter = ColorFilter.makeMatrix(
            ColorMatrix(
                saturation, 0f, 0f, 0f, 0f,
                0f, saturation, 0f, 0f, 0f,
                0f, 0f, saturation, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
    canvas.drawImage(Image.makeFromBitmap(bitmap.asSkiaBitmap()), 0f, 0f, paint)
    return surface.makeImageSnapshot().toComposeImageBitmap()
}

fun pixelsToImageBitmap(pixels: IntArray, width: Int, height: Int): ImageBitmap {
    val byteBuffer = ByteArray(pixels.size * 4)

    for (i in pixels.indices) {
        val argb = pixels[i]
        val index = i * 4

        byteBuffer[index] = (argb shr 16 and 0xFF).toByte()  // R
        byteBuffer[index + 1] = (argb shr 8 and 0xFF).toByte()  // G
        byteBuffer[index + 2] = (argb and 0xFF).toByte()  // B
        byteBuffer[index + 3] = (argb shr 24 and 0xFF).toByte()  // A
    }

    val skiaBitmap = Bitmap().apply {
        allocPixels(
            ImageInfo(
                width,
                height,
                ColorType.RGBA_8888,
                ColorAlphaType.PREMUL
            )
        )
        installPixels(byteBuffer)
    }

    val skiaImage = Image.makeFromBitmap(skiaBitmap)
    return skiaImage.toComposeImageBitmap()
}

fun adjustImageContrastGamma(bitmap: ImageBitmap?, contrast: Float): ImageBitmap? {
    if (bitmap == null) return null

    val width = bitmap.width
    val height = bitmap.height

    val pixels = IntArray(width * height)
    bitmap.readPixels(pixels, startX = 0, startY = 0, width = width, height = height)

    // Вычисляем гамма-фактор (чем выше contrast, тем сильнее изменение)
    val gamma = 1.0 / contrast
    val gammaCorrection = FloatArray(256) { i ->
        ((i / 255.0).pow(gamma) * 255).toInt().coerceIn(0, 255).toFloat()
    }

    // Применяем гамма-коррекцию к каждому пикселю
    for (i in pixels.indices) {
        val argb = pixels[i]
        val a = (argb shr 24) and 0xFF
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = (argb and 0xFF)

        val newR = gammaCorrection[r].toInt()
        val newG = gammaCorrection[g].toInt()
        val newB = gammaCorrection[b].toInt()

        pixels[i] = (a shl 24) or (newR shl 16) or (newG shl 8) or newB
    }

    return pixelsToImageBitmap(pixels, width, height)
}

fun calculateColor(imageBitmap: ImageBitmap?, offset: Offset, size: Size): Color {
    try {
        if (imageBitmap == null) return COLOR_DEFAULT
        val skiaBitmap = imageBitmap.asSkiaBitmap()
        var sumR = 0L
        var sumG = 0L
        var sumB = 0L
        var sumA = 0L
        var pixelCount = 0L
        val xStart = (skiaBitmap.width / 2 + offset.x).toInt()
        val xEnd = (skiaBitmap.width / 2 + offset.x + size.width).toInt()
        val yStart = (skiaBitmap.height / 2 + offset.y).toInt()
        val yEnd = (skiaBitmap.height / 2 + offset.y + size.height).toInt()
        for (i in xStart until xEnd.coerceAtMost(skiaBitmap.width)) {
            for (j in yStart until yEnd.coerceAtMost(skiaBitmap.height)) {
                val pixel = skiaBitmap.getColor(i, j)
                val color = Color(pixel)
                sumR += (color.red * 255).toInt()
                sumG += (color.green * 255).toInt()
                sumB += (color.blue * 255).toInt()
                sumA += (color.alpha * 255).toInt()
                pixelCount++
            }
        }
        if (pixelCount == 0L) return COLOR_DEFAULT
        return Color(
            red = (sumR / pixelCount) / 255f,
            green = (sumG / pixelCount) / 255f,
            blue = (sumB / pixelCount) / 255f,
            alpha = (sumA / pixelCount) / 255f,
        )
    } catch (e: Exception) {
        println(e.message)
        return COLOR_DEFAULT
    }
}

fun colorDistance(c1: Color, c2: Color): Float {
    val dr = c1.red - c2.red
    val dg = c1.green - c2.green
    val db = c1.blue - c2.blue
    return sqrt(dr * dr + dg * dg + db * db)
}
