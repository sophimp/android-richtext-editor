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
class LineSpaceEnlargeStyle(editText: RichEditText) : BaseParagraphStyle<LineSpaceSpan>(editText) {
    private val EPSILON = 1e-5

    override fun <T : ISpan> updateSpan(spans: Array<T>, start: Int, end: Int) {
        if (spans.isNotEmpty()) {
            val ori = spans[0] as LineSpaceSpan
            val factor = getNextLargeFactor(ori.factor)
            if (factor >= 5.5) {
                Util.toast(context, "reached the max line space")
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

    private fun getNextLargeFactor(factor: Float): Float {
        when {
            abs(factor - 1f) < EPSILON -> {
                return 1.25f
            }
            abs(factor - 1.25f) < EPSILON -> {
                return 1.5f
            }
            abs(factor - 1.5f) < EPSILON -> {
                return 2.0f
            }
            abs(factor - 2f) < EPSILON -> {
                return 3.0f
            }
            abs(factor - 3f) < EPSILON -> {
                return 4.0f
            }
            abs(factor - 4f) < EPSILON -> {
                return 5.5f
            }
            abs(factor - 5.5f) < EPSILON -> {
                return 5.5f
            }
            else -> return 1f
        }
    }

    override fun handleSingleParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {

        super.handleSingleParagraphInput(editable, changedText, beforeSelectionStart, afterSelectionEnd, epStart, epEnd)

        // if current line has no LineSpaceSpan, need to add one, for resolve the problem of the overlap when you apply this style on where two paragraphs separate with consecutive empty line
        // also we should limit factor <= 1.0 LineSpaceSpan not to be converted to html rich text to avoid the html too long
        if (epStart < epEnd) {
            val lineSpaceSpans = editable.getSpans(epStart, epEnd, LineSpaceSpan::class.java)
            if (lineSpaceSpans.isEmpty()) {
                setSpan(LineSpaceSpan(1.0f), epStart, epEnd)
            }
        }
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