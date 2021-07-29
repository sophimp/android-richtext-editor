package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontBackgroundColorSpan

class FontBackgroundStyle(editText: RichEditText) :
    DynamicCharacterStyle<FontBackgroundColorSpan>(editText) {

    override fun newSpan(): FontBackgroundColorSpan? {
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
        if (color != 0 && color == mColor) return
        hasChanged = true
        checkState = color != 0
        mColor = color
        toolItemIconClick()
    }

    override fun targetClass(): Class<FontBackgroundColorSpan> {
        return FontBackgroundColorSpan::class.java
    }

}