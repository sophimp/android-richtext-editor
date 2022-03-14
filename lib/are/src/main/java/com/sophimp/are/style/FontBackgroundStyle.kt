package com.sophimp.are.style

import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontBackgroundColorSpan
import com.sophimp.are.spans.IDynamicSpan
import com.sophimp.are.spans.ISpan

class FontBackgroundStyle(editText: RichEditText) :
    DynamicCharacterStyle<FontBackgroundColorSpan>(editText) {
    init {
        mFeature = Constants.DEFAULT_FONT_COLOR
    }

    override fun newSpan(inheritSpan: ISpan?): FontBackgroundColorSpan? {
        return when {
            inheritSpan != null ->
                FontBackgroundColorSpan((inheritSpan as IDynamicSpan).dynamicFeature)
            (mFeature.isEmpty() || mFeature == Constants.DEFAULT_FONT_COLOR) ->
                null
            else ->
                FontBackgroundColorSpan(mFeature)
        }
    }

    override fun onFeatureChanged(feature: String) {
        this.mFeature = feature
        checkState = this.mFeature != Constants.DEFAULT_FONT_COLOR
        handleAbsButtonClick(mEditText.selectionStart, mEditText.selectionEnd)
    }

    override fun targetClass(): Class<FontBackgroundColorSpan> {
        return FontBackgroundColorSpan::class.java
    }

}