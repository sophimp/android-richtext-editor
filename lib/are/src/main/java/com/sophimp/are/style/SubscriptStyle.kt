package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.SubscriptSpan2

class SubscriptStyle(
    editText: RichEditText
) : ABSStyle<SubscriptSpan2>(editText, SubscriptSpan2::class.java) {

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
    }

    override fun newSpan(): SubscriptSpan2? {
        return SubscriptSpan2()
    }

    override fun insertSpan(span: ISpan, start: Int, end: Int) {

    }

}