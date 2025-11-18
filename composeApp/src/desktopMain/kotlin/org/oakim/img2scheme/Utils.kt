package org.oakim.img2scheme

import org.jetbrains.skia.Font
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.FontStyle
import org.jetbrains.skia.TextLine
import org.jetbrains.skia.Typeface
import java.util.Queue

fun Queue<() -> Unit>.executeAll() {
    while (!this.isEmpty())
        this.poll()()
}

public object PrintGraphicsUtils {
    const val baseDimSize = 20.0f
    const val lineWidth = baseDimSize * 0.1f

    public val typeface: Typeface by lazy {
        FontMgr.default.apply {
            println("Family names:")
            (0..familiesCount).forEach { i ->
                println("\t${getFamilyName(i)}")
            }
        }
        FontMgr.default.matchFamilyStyle(
//            FontMgr.default.getFamilyName(0),
            "Arial",
            FontStyle.BOLD
        ) ?: error("Cannot load typeface")
    }

    public val font by lazy {
        Font(typeface, baseDimSize * 0.8f)
    }

    public val headerFont by lazy {
        Font(typeface, baseDimSize * 0.5f)
    }

    private val textLineCache by lazy {
        mutableMapOf<Int, TextLine>()
    }

    public fun textLine(n: Int) =
        textLineCache.computeIfAbsent(n) {
            TextLine.make(n.toString(), font)
        }

    private val headerLineCache by lazy {
        mutableMapOf<Int, TextLine>()
    }

    public fun headerLine(n: Int) =
        headerLineCache.computeIfAbsent(n) {
            TextLine.make(n.toString(), headerFont)
        }
}