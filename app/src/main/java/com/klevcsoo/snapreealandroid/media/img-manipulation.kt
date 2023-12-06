package com.klevcsoo.snapreealandroid.media

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter

fun dimmedFilter(): ColorMatrixColorFilter {
    val fb = -50F
    val cmB = ColorMatrix()
    cmB.set(
        floatArrayOf(
            1f, 0f, 0f, 0f, fb,
            0f, 1f, 0f, 0f, fb,
            0f, 0f, 1f, 0f, fb,
            0f, 0f, 0f, 1f, 0f
        )
    )
    val colorMatrix = ColorMatrix()
    colorMatrix.set(cmB)
    return ColorMatrixColorFilter(colorMatrix)
}
