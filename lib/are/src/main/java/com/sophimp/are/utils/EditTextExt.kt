package com.sophimp.are.utils

import android.widget.EditText

/**
 * create by sfx on 2023/5/22 18:23
 */

fun EditText.locationYInScrollView(scrollYInScrollView: Int): Int {
//    val location = IntArray(2)
//    getLocationInWindow(location)
    val startLine = layout.getLineForOffset(selectionStart)
    val lineTop = layout.getLineTopWithoutPadding(startLine) - 1
//    val yInScrollYView = location[1] + paddingTop + compoundPaddingTop + scrollYInScrollView //+ lineTop
//    LogUtils.d("lineTop: $lineTop scrollYInScrollView: $scrollYInScrollView yInScrollView: ${scrollYInScrollView + lineTop}")
    return if (height - lineTop > 300) {
        scrollYInScrollView + lineTop - 300
    } else {
        scrollYInScrollView + lineTop
    }
}