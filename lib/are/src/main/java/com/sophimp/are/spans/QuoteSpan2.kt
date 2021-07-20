package com.sophimp.are.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.QuoteSpan
import com.sophimp.are.Constants

/**
 * custom style
 * @author: sfx
 * @since: 2021/7/20
 */
class QuoteSpan2 : QuoteSpan() {
    override fun getLeadingMargin(first: Boolean): Int {
        return 45 // hard-coded..
    }

    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout
    ) {
        val INDENT = 30
        c.translate(INDENT.toFloat(), 0f)
        val style = p.style
        val color = p.color
        p.style = Paint.Style.FILL
        p.color = Constants.COLOR_QUOTE
        c.drawRect(
            x.toFloat(),
            top.toFloat(),
            x + dir * 2 + 5.toFloat(),
            bottom.toFloat(),
            p
        ) // Hard-coded - right
        p.style = style
        p.color = color
        c.translate(-INDENT.toFloat(), 0f)
    }
}