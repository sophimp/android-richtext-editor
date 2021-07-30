package com.sophimp.are.style

import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontSizeSpan
import com.sophimp.are.spans.ISpan

class FontSizeStyle(editText: RichEditText) :
    DynamicCharacterStyle<FontSizeSpan>(editText) {

    private var mSize = Constants.DEFAULT_FEATURE

    override fun newSpan(inheritSpan: ISpan?): FontSizeSpan? {
        return FontSizeSpan(mSize)
    }

    override fun onFeatureChanged(feature: Int) {
        mSize = feature
        checkState = mSize != Constants.DEFAULT_FEATURE
        handleAbsButtonClick()
    }

    override fun newSpan(size: Int): FontSizeSpan? {
        return FontSizeSpan(size)
    }

    override fun targetClass(): Class<FontSizeSpan> {
        return FontSizeSpan::class.java
    }

}