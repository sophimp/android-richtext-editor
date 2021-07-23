package com.sophimp.are.style

import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontSizeSpan
import com.sophimp.are.style.windows.FontSizeChangeListener

class FontSizeStyle(editText: RichEditText) :
    ABSDynamicStyle<FontSizeSpan>(editText, FontSizeSpan::class.java), FontSizeChangeListener {

    private var mSize = Constants.DEFAULT_FONT_SIZE

    override fun newSpan(): FontSizeSpan? {
        return FontSizeSpan(mSize)
    }

    override fun onFontSizeChange(fontSize: Int) {
        checkState = true
        if (mSize == fontSize) return
        hasChanged = true
        mSize = fontSize
        val editable = mEditText.editableText
        val start = mEditText.selectionStart
        val end = mEditText.selectionEnd
        if (end > start) {
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

    override fun featureChangedHook(lastSpanFontSize: Int) {
        mSize = lastSpanFontSize
    }

    override fun newSpan(size: Int): FontSizeSpan? {
        return FontSizeSpan(size)
    }

}