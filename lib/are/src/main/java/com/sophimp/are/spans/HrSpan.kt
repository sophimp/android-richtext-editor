package com.sophimp.are.spans

import android.content.Context
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
class HrSpan(ctx: Context) : ReplacementSpan(), ISpan {
    private val mScreenWidth: Int
    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {
        return (mScreenWidth * p).toInt()
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
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        val lineY = top + (bottom - top) / 2
        val linePath = Path()
        linePath.moveTo(x + (mScreenWidth * (1 - p) / 2).toInt(), lineY.toFloat())
        linePath.lineTo(x + (mScreenWidth * p).toInt(), lineY.toFloat())
        paint.pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 1F)
        canvas.drawPath(linePath, paint)
        //        canvas.drawLine(x + (int) (mScreenWidth * (1 - p) / 2), lineY, x + (int) (mScreenWidth * p), lineY, paint);
    }

    override val html: String?
        get() {
            return "<hr/>"
        }

    companion object {
        private const val p = 1f
    }

    init {
        mScreenWidth = ctx.resources.displayMetrics.widthPixels
    }
}