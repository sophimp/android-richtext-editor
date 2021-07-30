package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontBackgroundColorSpan
import com.sophimp.are.spans.ISpan

class FontBackgroundStyle(editText: RichEditText) :
    DynamicCharacterStyle<FontBackgroundColorSpan>(editText) {

    override fun newSpan(inheritSpan: ISpan?): FontBackgroundColorSpan? {
        return if (mColor == 0) null else FontBackgroundColorSpan(mColor)
    }

    override fun featureChangedHook(lastSpanFontColor: Int) {
        mColor = lastSpanFontColor
        onPickColor(lastSpanFontColor)
    }

    override fun newSpan(color: Int): FontBackgroundColorSpan? {
        mColor = color
        return if (color == 0) null else FontBackgroundColorSpan(color)
    }

    fun onPickColor(color: Int) {
        mColor = color
        checkState = mColor != 0
        handleAbsButtonClick()
    }

    override fun targetClass(): Class<FontBackgroundColorSpan> {
        return FontBackgroundColorSpan::class.java
    }

}