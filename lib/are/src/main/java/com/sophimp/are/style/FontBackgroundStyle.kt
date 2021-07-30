package com.sophimp.are.style

import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontBackgroundColorSpan
import com.sophimp.are.spans.ISpan

class FontBackgroundStyle(editText: RichEditText) :
    DynamicCharacterStyle<FontBackgroundColorSpan>(editText) {

    override fun newSpan(inheritSpan: ISpan?): FontBackgroundColorSpan? {
        return if (mColor == Constants.DEFAULT_FEATURE) null else FontBackgroundColorSpan(mColor)
    }

    override fun onFeatureChanged(feature: Int) {
        mColor = feature
        checkState = mColor != Constants.DEFAULT_FEATURE
        handleAbsButtonClick()
    }

    override fun newSpan(color: Int): FontBackgroundColorSpan? {
        mColor = color
        return if (color == Constants.DEFAULT_FEATURE) null else FontBackgroundColorSpan(color)
    }

    override fun targetClass(): Class<FontBackgroundColorSpan> {
        return FontBackgroundColorSpan::class.java
    }

}