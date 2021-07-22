package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.colorpicker.ColorPickerListener
import com.sophimp.are.spans.FontForegroundColorSpan

class FontColorStyle(editText: RichEditText?) :
    ABSDynamicStyle<FontForegroundColorSpan>(editText!!, FontForegroundColorSpan::class.java),
    ColorPickerListener {
    override fun toolItemIconClick() {
        super.toolItemIconClick()
        showFontColorPickerWindow()
    }

    private fun showFontColorPickerWindow() {
//        int yOff = Util.INSTANCE.getPixelByDp(getContext(), -5);
    }

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
        val editable = mEditText.editableText
        val start = mEditText.selectionStart
        val end = mEditText.selectionEnd
        if (end >= start) {
            applyStyle(
                editable,
                IStyle.TextEvent.IDLE,
                "",
                mEditText.selectionStart,
                mEditText.selectionStart,
                mEditText.selectionEnd
            )
        }
    }

    override fun insertSpan(span: FontForegroundColorSpan, start: Int, end: Int) {
    }
}