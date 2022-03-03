package com.sophimp.are.style

import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontSizeSpan
import com.sophimp.are.spans.IDynamicSpan
import com.sophimp.are.spans.ISpan

class FontSizeStyle(editText: RichEditText) :
    DynamicCharacterStyle<FontSizeSpan>(editText) {

    init {
        mFeature = "${Constants.DEFAULT_FONT_SIZE}"
    }

    private var mSize = Constants.DEFAULT_FONT_SIZE

    override fun newSpan(inheritSpan: ISpan?): FontSizeSpan? {
        return when {
            inheritSpan != null ->
                FontSizeSpan((inheritSpan as IDynamicSpan).dynamicFeature.toInt())
            (mSize == 0) ->
                null
            else ->
                FontSizeSpan(mSize)
        }
    }

    override fun onFeatureChanged(feature: String) {
        mSize = feature.toInt()
        checkState = mSize != Constants.DEFAULT_FONT_SIZE
        handleAbsButtonClick(mEditText.selectionStart, mEditText.selectionEnd)
    }

    override fun targetClass(): Class<FontSizeSpan> {
        return FontSizeSpan::class.java
    }

}