package com.sophimp.are.style

import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontForegroundColorSpan
import com.sophimp.are.spans.ISpan

class FontColorStyle(editText: RichEditText) : DynamicCharacterStyle<FontForegroundColorSpan>(editText) {

    override fun newSpan(inheritSpan: ISpan?): FontForegroundColorSpan? {
        return FontForegroundColorSpan(mColor)
    }

    override fun newSpan(color: Int): FontForegroundColorSpan? {
        return FontForegroundColorSpan(color)
    }

    override fun targetClass(): Class<FontForegroundColorSpan> {
        return FontForegroundColorSpan::class.java
    }

    override fun onFeatureChanged(feature: Int) {
        mColor = feature
        checkState = mColor != Constants.DEFAULT_FEATURE
        handleAbsButtonClick()
    }

}