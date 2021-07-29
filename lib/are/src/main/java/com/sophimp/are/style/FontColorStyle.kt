package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.colorpicker.ColorPickerListener
import com.sophimp.are.spans.FontForegroundColorSpan

class FontColorStyle(editText: RichEditText?) :
    DynamicCharacterStyle<FontForegroundColorSpan>(editText!!),
    ColorPickerListener {

    override fun newSpan(): FontForegroundColorSpan? {
        return FontForegroundColorSpan(mColor)
    }

    override fun featureChangedHook(lastSpanFontColor: Int) {
        mColor = lastSpanFontColor
    }

    override fun newSpan(color: Int): FontForegroundColorSpan? {
        return FontForegroundColorSpan(color)
    }

    override fun onPickColor(color: Int) {
        if (mColor == color) return
        hasChanged = true
        checkState = true
        mColor = color
        toolItemIconClick()
    }

    override fun targetClass(): Class<FontForegroundColorSpan> {
        return FontForegroundColorSpan::class.java
    }

}