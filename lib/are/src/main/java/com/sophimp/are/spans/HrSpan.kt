package com.sophimp.are.spans

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Path
import android.text.style.ReplacementSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
class HrSpan(var width: Int) : ReplacementSpan(), ISpan {

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {

//        if (fm != null) {
//            fm.ascent = -paint.fontMetricsInt.ascent
//            fm.descent = 0
//            fm.top = fm.ascent
//            fm.bottom = 0
//        }
        return width
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.save()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        val lineY = top + (bottom - top) / 2
        val linePath = Path()
        linePath.moveTo(x, lineY.toFloat())
        linePath.lineTo(x + width, lineY.toFloat())
        paint.pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 1F)
        canvas.drawPath(linePath, paint)
//        canvas.drawLine(x , lineY.toFloat(), x + width, lineY.toFloat(), paint);
        canvas.restore()
    }

    override val html: String
        get() {
            return "<hr/>"
        }

}