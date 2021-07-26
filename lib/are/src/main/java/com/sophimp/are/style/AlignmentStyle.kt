package com.sophimp.are.style

import android.text.Layout
import android.text.Spanned
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.*

class AlignmentStyle(
    editText: RichEditText,
    private var mAlignment: Layout.Alignment
) : BaseParagraphStyle<AlignmentSpan2>(editText) {

    private var off = 0

    override fun itemClickOnNonEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        val editable = mEditText.editableText
        // Indent and Alignment is mutex, only one can exit on the same time
        val indents = editable.getSpans(curPStart, curPEnd, IndentSpan::class.java)
        removeSpans(editable, indents)
        val aligns = mEditText.editableText.getSpans(curPStart, curPEnd, targetClass())
        updateSpan(
            aligns,
            curPStart,
            curPEnd,
            aligns.isNotEmpty() && aligns[0].alignment == this.mAlignment
        )
        return off
    }

    override fun newSpan(): ISpan? {
        return when (mAlignment) {
            Layout.Alignment.ALIGN_NORMAL -> {
                null
            }
            Layout.Alignment.ALIGN_CENTER -> {
                AlignmentCenterSpan()
            }
            Layout.Alignment.ALIGN_OPPOSITE -> {
                AlignmentRightSpan()
            }
            else -> null
        }
    }

    override fun setSpan(span: ISpan, start: Int, end: Int) {
        mEditText.editableText.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }

    override fun targetClass(): Class<AlignmentSpan2> {
        return AlignmentSpan2::class.java
    }
}