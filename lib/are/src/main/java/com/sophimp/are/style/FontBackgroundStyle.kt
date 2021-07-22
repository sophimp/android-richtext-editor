package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontBackgroundColorSpan

class FontBackgroundStyle(editText: RichEditText) :
    ABSDynamicStyle<FontBackgroundColorSpan>(editText, FontBackgroundColorSpan::class.java) {

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

    override fun insertSpan(span: FontBackgroundColorSpan, start: Int, end: Int) {

    }

}