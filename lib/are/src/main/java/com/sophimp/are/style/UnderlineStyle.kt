package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.UnderlineSpan2

class UnderlineStyle(editText: RichEditText) :
    ABSStyle<UnderlineSpan2>(editText, UnderlineSpan2::class.java) {

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
        mEditText.isChange = true
    }

    override fun newSpan(): UnderlineSpan2? {
        return UnderlineSpan2()
    }

    override fun insertSpan(span: UnderlineSpan2, start: Int, end: Int) {

    }
}