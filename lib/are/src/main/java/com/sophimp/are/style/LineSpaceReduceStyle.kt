package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.LineSpaceSpan
import kotlin.math.abs

/**
 * @author: sfx
 * @since: 2021/6/15
 */
class LineSpaceReduceStyle(editText: RichEditText) : BaseParagraphStyle<LineSpaceSpan>(editText) {
    private val EPSILON = 1e-5
    override fun <T : ISpan> updateSpan(spans: Array<T>, start: Int, end: Int) {
        if (spans.isNotEmpty()) {
            val ori = spans[0] as LineSpaceSpan
            val factor = getNextSmallFactor(ori.factor)
            if (factor <= 1.0) {
                Util.toast(context, "reached the min line space")
                return
            }
            removeSpans(mEditText.editableText, spans)
            ori.factor = factor
            setSpan(ori, start, end)
        } else {
            val ns = newSpan()
            if (ns != null) {
                setSpan(ns, start, end)
            }
        }
    }

    private fun getNextSmallFactor(factor: Float): Float {
        when {
            abs(factor - 5.5f) < EPSILON -> {
                return 4.0f
            }
            abs(factor - 4f) < EPSILON -> {
                return 3.0f
            }
            abs(factor - 3f) < EPSILON -> {
                return 2.0f
            }
            abs(factor - 2f) < EPSILON -> {
                return 1.5f
            }
            abs(factor - 1.5f) < EPSILON -> {
                return 1.25f
            }
            abs(factor - 1.25f) < EPSILON -> {
                return 1f
            }
            abs(factor - 1f) < EPSILON -> {
                return 1f
            }
            else -> return 1f
        }
    }

    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {
        // 交由LineSpaceEnlargeStyle 处理， 相同逻辑
    }

    override fun newSpan(inheritSpan: ISpan?): ISpan? {
        if (inheritSpan is LineSpaceSpan) {
            return LineSpaceSpan(inheritSpan.factor)
        }
        return LineSpaceSpan(1.0f)
    }

    override fun targetClass(): Class<LineSpaceSpan> {
        return LineSpaceSpan::class.java
    }

}