package com.example.rgbproject

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

class ColorAnalyzer(private val listener: ColorListener) : ImageAnalysis.Analyzer {

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    private fun getColorPercentageFromImage(image: ImageProxy): List<ColorPercentage> {
        val planes = image.planes

        val yArrByteArray = planes[0].buffer.toByteArray()

        val uArrByteArray = planes[1].buffer.toByteArray()

        val vArrByteArray = planes[2].buffer.toByteArray()

        val colorsMap = mutableMapOf<RGBColor, Int>()

        for (i in uArrByteArray.indices) {
            val y = yArrByteArray[2 * i].toInt() and 255
            val u = (uArrByteArray[i].toInt() and 255) - 128
            val v = (vArrByteArray[i].toInt() and 255) - 128
            val yuvColor = YUVColor(y.toShort(), u.toShort(), v.toShort())

            val hexColor = yuvColor.toRGB()
            if (colorsMap[hexColor] != null) {
                var x: Int = colorsMap[hexColor]!!
                colorsMap[hexColor] = ++x
            } else {
                colorsMap[hexColor] = 1
            }
        }

        val sortColors = colorsMap.toList().sortedByDescending { (_, value) -> value }

        return List(5) { i ->
            val color = sortColors[i]
            ColorPercentage(color.first, color.second.toFloat()/uArrByteArray.size)
        }
    }

    // analyze the color
    override fun analyze(image: ImageProxy) {
        val colors = getColorPercentageFromImage(image)
        listener(colors)
        image.close()
    }
}