package org.oakim.img2scheme.serialization

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.jetbrains.skia.Image
import java.util.Base64

class ImageBitmapSerializer : JsonSerializer<ImageBitmap>() {
    override fun serialize(value: ImageBitmap?, gen: JsonGenerator, serializers: SerializerProvider) {
        if (value != null) {
            val skiaImage = Image.makeFromBitmap(value.asSkiaBitmap())
            val encodedImage = Base64.getEncoder().encodeToString(skiaImage.encodeToData()?.bytes ?: byteArrayOf())
            gen.writeString(encodedImage)
        } else {
            gen.writeNull()
        }
    }
}

class ImageBitmapDeserializer : JsonDeserializer<ImageBitmap?>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ImageBitmap? {
        val encodedImage = p.text ?: return null
        val decodedBytes = Base64.getDecoder().decode(encodedImage)
        val skiaImage = Image.makeFromEncoded(decodedBytes)
        return skiaImage.toComposeImageBitmap()
    }
}