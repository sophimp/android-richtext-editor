package com.sophimp.are.style

import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontForegroundColorSpan
import com.sophimp.are.spans.IDynamicSpan
import com.sophimp.are.spans.ISpan

class FontColorStyle(editText: RichEditText) : DynamicCharacterStyle<FontForegroundColorSpan>(editText) {
    init {
        mFeature = Constants.DEFAULT_FONT_COLOR
    }

    override fun newSpan(inheritSpan: ISpan?): FontForegroundColorSpan? {
        return when {
            inheritSpan != null ->
                FontForegroundColorSpan((inheritSpan as IDynamicSpan).dynamicFeature)
            mFeature == Constants.DEFAULT_FONT_COLOR ->
                null
            else ->
                FontForegroundColorSpan(mFeature)
        }
    }

    override fun targetClass(): Class<FontForegroundColorSpan> {
        return FontForegroundColorSpan::class.java
    }

    override fun onFeatureChanged(feature: String) {
        this.mFeature = feature
        checkState = this.mFeature != Constants.DEFAULT_FONT_COLOR
        handleAbsButtonClick(mEditText.selectionStart, mEditText.selectionEnd)
    }

}