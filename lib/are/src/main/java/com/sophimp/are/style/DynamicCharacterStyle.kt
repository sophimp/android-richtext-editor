package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.IDynamicSpan
import com.sophimp.are.spans.ISpan
import kotlin.math.max

/**
 * Dynamic style abstract implementation.
 *
 *
 * Dynamic means the Span has a configurable value which can provide different features.
 * Such as: Font color / Font size.
 *
 * @param <E>
</E> */
abstract class DynamicCharacterStyle<E : IDynamicSpan>(editText: RichEditText) :
    BaseCharacterStyle<E>(editText) {

    override fun toolItemIconClick() {
        checkState = !checkState
        mEditText.isChange = true
        logAllSpans(mEditText.editableText, "${targetClass().simpleName} item click", 0, mEditText.editableText.length)
    }

    override fun checkFeatureEqual(span1: ISpan?, span2: ISpan?): Boolean {
        return span1 != null && span2 != null && (span1 as IDynamicSpan).dynamicFeature == (span2 as IDynamicSpan).dynamicFeature
    }

    abstract fun onFeatureChanged(feature: String, start: Int, end: Int)

    override fun onSelectionChanged(selectionEnd: Int) {
        val start = max(0, selectionEnd - 1)
        val targetSpans = mEditText.editableText.getSpans(start, selectionEnd, targetClass())
        if (targetSpans.isNotEmpty()) {
            onFeatureChanged(targetSpans[0].dynamicFeature, selectionEnd, selectionEnd)
        } else {
            onFeatureChanged("", selectionEnd, selectionEnd)
        }
    }
}