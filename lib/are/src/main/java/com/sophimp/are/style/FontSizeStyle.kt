package com.sophimp.are.style

import android.text.TextUtils
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
            (mSize == 0 || mSize == Constants.DEFAULT_FONT_SIZE) ->
                null
            else ->
                FontSizeSpan(mSize)
        }
    }

    override fun onFeatureChanged(feature: String) {
        var validSize = Constants.DEFAULT_FONT_SIZE
        if (!TextUtils.isEmpty(feature)) {
            validSize = feature.toInt()
        }
        checkState = validSize != Constants.DEFAULT_FONT_SIZE
        mSize = validSize
        handleAbsButtonClick(mEditText.selectionStart, mEditText.selectionEnd)
    }

    override fun targetClass(): Class<FontSizeSpan> {
        return FontSizeSpan::class.java
    }

}