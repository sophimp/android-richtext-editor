package com.sophimp.are.decoration

import androidx.annotation.ColorInt

data class SideLine(
    var isHave: Boolean = false,
    @ColorInt var color: Int = 0,
    var widthDp: Float = 0f,
    var startPaddingDp: Float = 0f,
    var endPaddingDp: Float = 0f
)