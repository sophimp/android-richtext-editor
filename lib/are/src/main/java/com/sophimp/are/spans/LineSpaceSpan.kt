package com.sophimp.are.spans

import android.graphics.Paint.FontMetricsInt
import android.text.style.LineHeightSpan

/**
 * line spacing
 * @author: sfx
 * @since: 2021/7/20
 */
class LineSpaceSpan(var factor: Float, lineHeight: Float) : LineHeightSpan, ISpan {
    private var targetDescent = -1
    var delta = -1f
    var originHeight = 0
    override fun chooseHeight(
        text: CharSequence,
        start: Int,
        end: Int,
        spanstartv: Int,
        lineHeight: Int,
        fm: FontMetricsInt
    ) {
        // 原始行高
        originHeight = fm.descent - fm.ascent
        if (originHeight <= 0) {
            return
        }
        if (delta < 0) {
            delta = originHeight * (factor - 1.0f)
            targetDescent = Math.round(fm.descent + delta)
        }
        fm.descent = targetDescent
    }

}