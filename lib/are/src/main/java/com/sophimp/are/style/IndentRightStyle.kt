package com.sophimp.are.style

import android.text.style.AlignmentSpan
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.IndentSpan

class IndentRightStyle(editText: RichEditText) : BaseParagraphStyle<IndentSpan>(editText) {

    override fun itemClickOnNonEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        // alignment style is superior to indent style
        val aligns = mEditText.editableText.getSpans(curPStart, curPEnd, AlignmentSpan::class.java)
        if (aligns.isNotEmpty()) return 0

        return super.itemClickOnNonEmptyParagraph(curPStart, curPEnd)
    }

    override fun <T : ISpan> updateSpan(spans: Array<T>, start: Int, end: Int) {
        if (spans.isNotEmpty()) {
            removeSpans(mEditText.editableText, spans)
            (spans[0] as IndentSpan).increaseLevel()
            setSpan(spans[0], start, end)
        } else {
            val ns = newSpan()
            if (ns != null) {
                setSpan(ns, start, end)
            }
        }
        Util.renumberAllListItemSpans(mEditText.editableText)
        mEditText.setSelection(mEditText.selectionStart, mEditText.selectionEnd)
    }

    override fun newSpan(): ISpan? {
        return IndentSpan(1)
    }

    override fun targetClass(): Class<IndentSpan> {
        return IndentSpan::class.java
    }

}