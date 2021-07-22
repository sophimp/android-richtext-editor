package com.sophimp.are.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.text.Layout
import android.text.Spanned
import android.text.style.AlignmentSpan
import androidx.annotation.RequiresApi
import com.sophimp.are.Util
import com.sophimp.are.spans.IndentSpan

/**
 * number list, with indent
 * @author: sfx
 * @since: 2021/7/20
 */
class ListNumberSpan(var number: Int = 1) : IListSpan {
    var numberStr = "1."
    var textLength = 20

    // 数字1测量不出来
    private val startMargin = 15

    override fun getLeadingMargin(first: Boolean): Int {
        return Math.max(IListSpan.LEADING_MARGIN, textLength + startMargin)
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun drawLeadingMargin(
        c: Canvas, p: Paint, x: Int, dir: Int, top: Int,
        baseline: Int, bottom: Int, text: CharSequence, start: Int, end: Int,
        first: Boolean, l: Layout
    ) {
        if ((text as Spanned).getSpanStart(this) == start) {
            val style = p.style
            p.style = Paint.Style.FILL
            if (number > 0) {
                val leadingMarginSpans =
                    text.getSpans(start, end, IndentSpan::class.java)
                numberStr = if (leadingMarginSpans != null && leadingMarginSpans.size > 0) {
                    when (leadingMarginSpans[0].mLevel % 3) {
                        1 -> Util.toAbcOrder(number) + "."
                        2 -> Util.toRomanOrder(number) + "."
                        else -> "$number."
                    }
                } else {
                    "$number."
                }
                textLength = (p.measureText(numberStr) + 0.5f).toInt()
                val alignmentSpans =
                    text.getSpans(start, end, AlignmentSpan::class.java)
                if (null != alignmentSpans && alignmentSpans.size > 0) {
                    if (alignmentSpans[0]
                            .alignment == Layout.Alignment.ALIGN_CENTER
                    ) {
                        val v = p.measureText(text, start, end)
                        c.drawText(
                            numberStr,
                            (l.width - v - textLength - IListSpan.STANDARD_GAP_WIDTH) / 2,
                            baseline.toFloat(),
                            p
                        )
                        p.style = style
                        return
                    } else if (alignmentSpans[0]
                            .alignment == Layout.Alignment.ALIGN_OPPOSITE
                    ) {
                        val v = p.measureText(text, start, end)
                        c.drawText(
                            numberStr,
                            l.width - v - textLength - IListSpan.STANDARD_GAP_WIDTH,
                            baseline.toFloat(),
                            p
                        )
                        p.style = style
                        return
                    }
                }
                var margin = 0
                if (leadingMarginSpans != null && leadingMarginSpans.isNotEmpty()) {
                    margin = leadingMarginSpans[0].getLeadingMargin(true)
                    //二次绘制，x已经偏移了，故不需要再重新加偏移量
                    if (x == margin) {
                        margin = 0
                    }
                    //                    if (leadingMarginSpans[0].mLevel == 0) {
//                        margin = LEADING_MARGIN;
//                    }
//                    c.drawText(numberStr, x + dir + margin - textLength - STANDARD_GAP_WIDTH, baseline, p);
                }
                //                else {
//                }
                c.drawText(
                    numberStr,
                    x + dir + margin + Math.max(
                        IListSpan.LEADING_MARGIN,
                        startMargin + textLength
                    ) - textLength - IListSpan.STANDARD_GAP_WIDTH.toFloat(),
                    baseline.toFloat(),
                    p
                )
            } else {
                c.drawText("\u2022", x + dir.toFloat(), baseline.toFloat(), p)
            }
            p.style = style
        }
        //        textLength = (int) (p.measureText(numberStr) + 0.5f);
    }

}