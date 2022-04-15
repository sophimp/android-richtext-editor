package com.sophimp.are.style

import android.text.TextUtils
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontBackgroundColorSpan
import com.sophimp.are.spans.IDynamicSpan
import com.sophimp.are.spans.ISpan

class FontBackgroundStyle(editText: RichEditText) :
    DynamicCharacterStyle<FontBackgroundColorSpan>(editText) {
    init {
        mFeature = Constants.DEFAULT_FONT_BG_COLOR
    }

    override fun newSpan(inheritSpan: ISpan?): FontBackgroundColorSpan? {
        return when {
            inheritSpan != null ->
                FontBackgroundColorSpan((inheritSpan as IDynamicSpan).dynamicFeature)
            (mFeature.isEmpty() || mFeature == Constants.DEFAULT_FONT_BG_COLOR) ->
                null
            else ->
                FontBackgroundColorSpan(mFeature)
        }
    }

    override fun onFeatureChanged(feature: String) {
        var validFeature = feature
        if (TextUtils.isEmpty(feature)) {
            validFeature = Constants.DEFAULT_FONT_BG_COLOR
        }
        checkState = validFeature != Constants.DEFAULT_FONT_BG_COLOR
        mFeature = validFeature
        handleAbsButtonClick(mEditText.selectionStart, mEditText.selectionEnd)
    }

    override fun targetClass(): Class<FontBackgroundColorSpan> {
        return FontBackgroundColorSpan::class.java
    }

}