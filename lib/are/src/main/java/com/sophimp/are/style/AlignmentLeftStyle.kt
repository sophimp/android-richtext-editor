package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.*

class AlignmentLeftStyle(editText: RichEditText) : BaseParagraphStyle<AlignmentLeftSpan>(editText) {

    override fun removeMutexSpans(curPStart: Int, curPEnd: Int) {
        val editable = mEditText.editableText
        // Indent and Alignment is mutex, only one can exit on the same time
        val indents = editable.getSpans(curPStart, curPEnd, IndentSpan::class.java)
        removeSpans(editable, indents)

        val centerSpans = editable.getSpans(curPStart, curPEnd, AlignmentCenterSpan::class.java)
        removeSpans(editable, centerSpans)

        val rightSpans = editable.getSpans(curPStart, curPEnd, AlignmentRightSpan::class.java)
        removeSpans(editable, rightSpans)
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

    override fun newSpan(): ISpan? {
        return null
    }

    override fun targetClass(): Class<AlignmentLeftSpan> {
        return AlignmentLeftSpan::class.java
    }
}