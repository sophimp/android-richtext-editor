package com.sophimp.are.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.text.style.AlignmentSpan
import android.text.style.QuoteSpan
import com.sophimp.are.Constants

/**
 * custom style
 * @author: sfx
 * @since: 2021/7/20
 */
class QuoteSpan2 : QuoteSpan(), IListSpan {
    private var width = 10
    override fun getLeadingMargin(first: Boolean): Int {
        return IListSpan.LEADING_MARGIN
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
        if ((text as Spanned).getSpanStart(this) == start) {
            val style = p.style
            val color = p.color
            p.style = Paint.Style.FILL
            p.color = Constants.COLOR_QUOTE
            val alignmentSpans = text.getSpans(start, end, AlignmentSpan::class.java)
            if (null != alignmentSpans) {
                for (span in alignmentSpans) {
                    val v = p.measureText(text, start, end)
                    if (span.alignment == Layout.Alignment.ALIGN_CENTER) {
                        val ix = (layout.width - v - width - IListSpan.STANDARD_GAP_WIDTH).toInt() / 2
                        c.drawRect(ix.toFloat(), top.toFloat(), (ix + width).toFloat(), bottom.toFloat(), p) // Hard-coded - right
                        return
                    } else if (alignmentSpans[0].alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                        val ix = (layout.width - v - width - IListSpan.STANDARD_GAP_WIDTH).toInt()
                        c.drawRect(ix.toFloat(), top.toFloat(), (ix + width).toFloat(), bottom.toFloat(), p) // Hard-coded - right
                        return
                    }
                }
            }
            var margin = 0
            val leadingMarginSpans = text.getSpans(start, end, IndentSpan::class.java)
            if (leadingMarginSpans != null && leadingMarginSpans.isNotEmpty()) {
                margin = leadingMarginSpans[0].getLeadingMargin(true)
                //二次绘制，ix已经偏移了，故不需要再重新加偏移量
                if (x == margin) {
                    margin = 0
                }
            }
            val ix = x + margin + IListSpan.LEADING_MARGIN - width - IListSpan.STANDARD_GAP_WIDTH
            c.drawRect(ix.toFloat(), top.toFloat(), (ix + width).toFloat(), bottom.toFloat(), p) // Hard-coded - right
            p.style = style
            p.color = color

        }
    }

}