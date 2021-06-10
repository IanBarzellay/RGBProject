package com.example.rgbproject

import kotlin.math.roundToInt

data class YUVColor(val y: Short, val u: Short, val v: Short) {
    fun toRGB(): RGBColor {
        val r = y + (1.370705 * v)
        val g = y - (0.698001 * v) - (0.337633 * u)
        val b = y + (1.732446 * u)
        return RGBColor(r.roundToShort(), g.roundToShort(), b.roundToShort())
    }

    private fun Double.roundToShort() = this.roundToInt().toShort()
}

data class RGBColor(val r: Short, val g: Short, val b: Short) {

    fun toColorInt(): Int {
        val red = r.toInt() shl 16 and 0x00FF0000 //Shift red 16-bits and mask out other stuff
        val green = g.toInt() shl 8 and 0x0000FF00 //Shift Green 8-bits and mask out other stuff
        val blue = b.toInt() and 0x000000FF //Mask out anything not blue.
        return -0x1000000 or red or green or blue //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    fun toVisibleString(): String {
        return "R: $r  G: $g  \nB: $b"
    }
}

data class ColorPercentage(val color: RGBColor, val percentage: Float)