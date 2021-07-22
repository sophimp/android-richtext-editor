package com.sophimp.are.style

import android.view.View
import android.widget.ImageView
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ItalicSpan

class ItalicStyle(editText: RichEditText) :
    ABSStyle<ItalicSpan>(editText, ItalicSpan::class.java) {

    override fun toolItemIconClick() {
        super.toolItemIconClick()
        applyStyle(
            mEditText.editableText,
            IStyle.TextEvent.IDLE,
            "",
            mEditText.selectionStart,
            mEditText.selectionStart,
            mEditText.selectionEnd
        )
        if (mEditText != null) {
            mEditText.isChange = true
        }
    }

    fun bindListenerForToolbarItemView(imageView: ImageView) {
        imageView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

            }
        })
    }

    override fun newSpan(): ItalicSpan? {
        return ItalicSpan()
    }

}