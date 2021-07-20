package com.sophimp.are.spans

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

/**
 * @param typeface The font family for this typeface.  Examples include:
 * ???
 */
class MyTypefaceSpan(private val sTypeface: Typeface?) : MetricAffectingSpan() {
    val spanTypeId: Int
        get() = 13

    fun describeContents(): Int {
        return 0
    }

    override fun updateMeasureState(p: TextPaint) {
        apply(p)
    }

    override fun updateDrawState(tp: TextPaint) {
        apply(tp)
    }

    private fun apply(paint: Paint) {
        if (null != sTypeface) {
            paint.typeface = sTypeface
        }
    }
}