package com.sophimp.are.style

import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontSizeSpan
import com.sophimp.are.spans.ISpan

class FontSizeStyle(editText: RichEditText) :
    DynamicCharacterStyle<FontSizeSpan>(editText) {

    private var mSize = Constants.DEFAULT_FONT_SIZE

    override fun newSpan(inheritSpan: ISpan?): FontSizeSpan? {
        return FontSizeSpan(mSize)
    }

    fun onFontSizeChange(fontSize: Int) {
        checkState = true
        if (mSize == fontSize) return
        hasChanged = true
        mSize = fontSize
        toolItemIconClick()
    }

    override fun featureChangedHook(lastSpanFontSize: Int) {
        mSize = lastSpanFontSize
    }

    override fun newSpan(size: Int): FontSizeSpan? {
        return FontSizeSpan(size)
    }

    override fun targetClass(): Class<FontSizeSpan> {
        return FontSizeSpan::class.java
    }

}