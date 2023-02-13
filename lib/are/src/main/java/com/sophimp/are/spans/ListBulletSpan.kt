package com.sophimp.are.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.text.style.AlignmentSpan

/**
 * bullet list, with indent
 * @author: sfx
 * @since: 2021/7/20
 */
class ListBulletSpan : IListSpan {
    // 实心圆\u2022 空心圆\u25E6 实心方块 \u25FE
    var sign = "\u2022"
//    override fun getLeadingMargin(first: Boolean): Int {
//        return IListSpan.LEADING_MARGIN
//    }

    override fun drawLeadingMargin(
        c: Canvas, p: Paint, x: Int, dir: Int, top: Int,
        baseline: Int, bottom: Int, text: CharSequence, start: Int, end: Int,
        first: Boolean, layout: Layout
    ) {
        super.drawLeadingMargin(c, p, x, dir, top, baseline, bottom, text, start, end, first, layout)
        if ((text as Spanned).getSpanStart(this) == start) {
            val style = p.style
            p.style = Paint.Style.FILL
            val leadingMarginSpans = text.getSpans(start, end, IndentSpan::class.java)
            if (leadingMarginSpans != null && leadingMarginSpans.isNotEmpty()) {
                sign = when (leadingMarginSpans[0].mLevel % 3) {
                    1 -> "◦"
                    2 -> "▪"
                    else -> // sign = "●";
                        "\u2022"
                }
            }
//            val textLength = (p.measureText("99") + 0.5f).toInt()
            val alignmentSpans = text.getSpans(start, end, AlignmentSpan::class.java)
            if (null != alignmentSpans) {
                for (span in alignmentSpans) {
                    if (span.alignment == Layout.Alignment.ALIGN_CENTER) {
                        val textWidth = p.measureText(text, start, end)
                        c.drawText(
                            sign,
                            (layout.width - textWidth - IListSpan.LEADING_MARGIN - IListSpan.STANDARD_GAP_WIDTH) / 2,
                            baseline.toFloat(),
                            p
                        )
                        p.style = style
                        return
                    } else if (alignmentSpans[0].alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                        val v = p.measureText(text, start, end)
                        //                        c.drawText(sign, layout.getWidth() - v - LEADING_MARGIN, baseline, p);
                        c.drawText(
                            sign,
                            layout.width - v - IListSpan.LEADING_MARGIN - IListSpan.STANDARD_GAP_WIDTH,
                            baseline.toFloat(),
                            p
                        )
                        p.style = style
                        return
                    }
                }
            }
            var indentMargin = 0
            if (leadingMarginSpans!!.isNotEmpty()) {
                indentMargin = leadingMarginSpans[0].getLeadingMargin(true)
                //二次绘制，x已经偏移了，故不需要再重新加偏移量
                if (x == indentMargin) {
                    indentMargin = 0
                }
            }
            // 没有缩进的情况
            c.drawText(
                sign,
                x + indentMargin + IListSpan.LEADING_MARGIN - IListSpan.STANDARD_GAP_WIDTH * 1.5f,
                baseline.toFloat(),
                p
            )
            p.style = style
        }
    }
}