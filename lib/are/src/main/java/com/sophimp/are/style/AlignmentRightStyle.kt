package com.sophimp.are.style

import android.text.Spanned
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.AlignmentCenterSpan
import com.sophimp.are.spans.AlignmentRightSpan
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.IndentSpan

class AlignmentRightStyle(editText: RichEditText) : BaseParagraphStyle<AlignmentRightSpan>(editText) {
    private var off = 0

    override fun removeMutexSpans(curPStart: Int, curPEnd: Int) {
        val editable = mEditText.editableText
        // Indent and Alignment is mutex, only one can exit on the same time
        val indents = editable.getSpans(curPStart, curPEnd, IndentSpan::class.java)
        removeSpans(editable, indents)

        val centerSpans = editable.getSpans(curPStart, curPEnd, AlignmentCenterSpan::class.java)
        removeSpans(editable, centerSpans)
    }

    override fun newSpan(): ISpan? {
        return AlignmentRightSpan()
    }

    override fun setSpan(span: ISpan, start: Int, end: Int) {
        mEditText.editableText.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }

    override fun targetClass(): Class<AlignmentRightSpan> {
        return AlignmentRightSpan::class.java
    }
}