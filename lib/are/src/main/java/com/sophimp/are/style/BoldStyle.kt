package com.sophimp.are.style

import android.widget.ImageView
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.BoldSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
class BoldStyle(editText: RichEditText) :
    ABSStyle<BoldSpan>(editText, BoldSpan::class.java) {
    fun bindListenerForToolbarItemView(imageView: ImageView) {
        imageView.setOnClickListener {
            applyStyle(
                mEditText.editableText,
                IStyle.TextEvent.IDLE,
                "",
                mEditText.selectionStart,
                mEditText.selectionStart,
                mEditText.selectionEnd
            )
            mEditText.isChange = true
        }
    }

    override fun newSpan(): BoldSpan? {
        return BoldSpan()
    }

    override fun insertSpan(span: BoldSpan, start: Int, end: Int) {

    }
}