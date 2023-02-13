package com.sophimp.are.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan

interface IListSpan : LeadingMarginSpan, ISpan {

    override fun getLeadingMargin(first: Boolean): Int {
        return (LEADING_MARGIN + STANDARD_GAP_WIDTH).coerceAtLeast(27)
    }

    override fun drawLeadingMargin(
        c: Canvas, p: Paint, x: Int, dir: Int, top: Int,
        baseline: Int, bottom: Int, text: CharSequence, start: Int, end: Int,
        first: Boolean, l: Layout
    ) {
        if (LEADING_MARGIN == 0) {
            LEADING_MARGIN = p.measureText("9999.").toInt()
            STANDARD_GAP_WIDTH = p.measureText("9").toInt()
        }
    }

    companion object {
        /**
         * 符号绘制区域
         */
        var LEADING_MARGIN = 0

        /**
         * 文字与符号间的距离
         */
        var STANDARD_GAP_WIDTH = 27
    }
}