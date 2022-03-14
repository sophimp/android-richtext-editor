package com.sophimp.are.style

import android.text.Editable
import android.text.style.AlignmentSpan
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.AlignmentLeftSpan
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.IndentSpan

class AlignmentLeftStyle(editText: RichEditText) : BaseParagraphStyle<AlignmentLeftSpan>(editText) {

    override fun removeMutexSpans(curPStart: Int, curPEnd: Int) {
        val editable = mEditText.editableText
        // Indent and Alignment is mutex, only one can exit on the same time
        val indents = editable.getSpans(curPStart, curPEnd, IndentSpan::class.java)
        removeSpans(editable, indents)

        val alignmentSpans = editable.getSpans(curPStart, curPEnd, AlignmentSpan::class.java)
        removeSpans(editable, alignmentSpans)

    }

    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {
        // default style, no need to handle
    }

    override fun newSpan(inheritSpan: ISpan?): ISpan? {
        return null
    }

    override fun targetClass(): Class<AlignmentLeftSpan> {
        return AlignmentLeftSpan::class.java
    }
}