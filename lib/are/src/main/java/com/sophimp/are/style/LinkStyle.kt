package com.sophimp.are.style

import android.text.Editable
import android.text.Spanned
import android.widget.ImageView
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.UrlSpan

/**
 * @author: sfx
 * @since: 2021/7/22
 */
class LinkStyle(editText: RichEditText) : BaseStyle<UrlSpan>(editText) {

    companion object {
        const val HTTP = "http://"
        const val HTTPS = "https://"
    }


    fun applyStyle(editable: Editable?, start: Int, end: Int) {
        // Do nothing
    }

    val imageView: ImageView?
        get() = null

    fun setChecked(isChecked: Boolean) {
        // Do nothing
    }

    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        start: Int,
        end: Int
    ) {
    }

    override fun insertSpan(span: UrlSpan, start: Int, end: Int) {
        mEditText.editableText.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mEditText.editableText.insert(end, Constants.ZERO_WIDTH_SPACE_STR)
    }
}