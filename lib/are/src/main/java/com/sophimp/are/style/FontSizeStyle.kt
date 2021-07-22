package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.FontSizeSpan
import com.sophimp.are.style.windows.FontSizeChangeListener
import com.sophimp.are.style.windows.FontSizePickerWindow

class FontSizeStyle(editText: RichEditText) :
    ABSDynamicStyle<FontSizeSpan>(editText, FontSizeSpan::class.java), FontSizeChangeListener {

    private var mSize = Constants.DEFAULT_FONT_SIZE

    private var mFontPickerWindow: FontSizePickerWindow? = null

    override fun toolItemIconClick() {
        super.toolItemIconClick()
        showFontsizePickerWindow()
    }

    private fun showFontsizePickerWindow() {
        if (mFontPickerWindow == null) {
            mFontPickerWindow = FontSizePickerWindow(context, this)
        }
        mFontPickerWindow!!.setFontSize(mSize)
        // todo move to toolbar item click
//        mFontPickerWindow!!.showAsDropDown(, 0, 0)
    }

    protected fun changeSpanInsideStyle(
        editable: Editable,
        start: Int,
        end: Int,
        existingSpan: FontSizeSpan
    ) {
        val currentSize = existingSpan.size
        if (currentSize != mSize) {
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

    override fun newSpan(): FontSizeSpan? {
        return FontSizeSpan(mSize)
    }

    override fun onFontSizeChange(fontSize: Int) {
        checkState = true
        if (mSize == fontSize) return
        hasChanged = true
        mSize = fontSize
        if (null != mEditText) {
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
    }

    override fun featureChangedHook(lastSpanFontSize: Int) {
        mSize = lastSpanFontSize
        if (mFontPickerWindow != null) {
            mFontPickerWindow!!.setFontSize(mSize)
        }
    }

    override fun newSpan(size: Int): FontSizeSpan? {
        return FontSizeSpan(size)
    }

    override fun insertSpan(span: FontSizeSpan, start: Int, end: Int) {
    }
}