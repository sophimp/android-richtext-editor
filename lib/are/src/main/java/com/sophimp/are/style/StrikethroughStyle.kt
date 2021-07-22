package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.StrikeThroughSpan2

class StrikethroughStyle(editText: RichEditText) :
    ABSStyle<StrikeThroughSpan2>(editText, StrikeThroughSpan2::class.java) {

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

    override fun newSpan(): StrikeThroughSpan2 {
        return StrikeThroughSpan2()
    }

    override fun insertSpan(span: ISpan, start: Int, end: Int) {
    }
}