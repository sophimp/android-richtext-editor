package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.IDynamicSpan
import com.sophimp.are.spans.ISpan

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

    abstract fun onFeatureChanged(feature: String)
}