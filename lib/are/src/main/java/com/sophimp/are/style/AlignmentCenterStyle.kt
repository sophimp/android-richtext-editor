package com.sophimp.are.style

import android.text.Spanned
import android.text.style.AlignmentSpan
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.AlignmentCenterSpan
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.IndentSpan

class AlignmentCenterStyle(editText: RichEditText) : BaseParagraphStyle<AlignmentCenterSpan>(editText) {

    override fun removeMutexSpans(curPStart: Int, curPEnd: Int) {
        val editable = mEditText.editableText
        // Indent and Alignment is mutex, only one can exit on the same time
        val indents = editable.getSpans(curPStart, curPEnd, IndentSpan::class.java)
        removeSpans(editable, indents)

        val rightSpans = editable.getSpans(curPStart, curPEnd, AlignmentSpan::class.java)
        removeSpans(editable, rightSpans)
    }

    override fun newSpan(inheritSpan: ISpan?): ISpan? {
        return AlignmentCenterSpan()
    }

    override fun setSpan(span: ISpan, start: Int, end: Int) {
        mEditText.editableText.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }

    override fun targetClass(): Class<AlignmentCenterSpan> {
        return AlignmentCenterSpan::class.java
    }
}