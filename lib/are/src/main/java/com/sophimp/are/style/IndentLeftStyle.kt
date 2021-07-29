package com.sophimp.are.style

import android.text.Editable
import android.text.style.AlignmentSpan
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.IndentSpan

class IndentLeftStyle(editText: RichEditText) : BaseParagraphStyle<IndentSpan>(editText) {

    override fun itemClickOnNonEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        // alignment style is superior to indent style
        val aligns = mEditText.editableText.getSpans(curPStart, curPEnd, AlignmentSpan::class.java)
        if (aligns.isNotEmpty()) return 0

        return super.itemClickOnNonEmptyParagraph(curPStart, curPEnd)
    }

    override fun <T : ISpan> updateSpan(spans: Array<T>, start: Int, end: Int) {
        if (spans.isNotEmpty()) {
            removeSpans(mEditText.editableText, spans)
            (spans[0] as IndentSpan).decreaseLevel()
            setSpan(spans[0], start, end)
            Util.renumberAllListItemSpans(mEditText.editableText)
            mEditText.setSelection(mEditText.selectionStart, mEditText.selectionEnd)
        }
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
        // left无需操作， 由 IndentRightStyle 处理
    }

    override fun targetClass(): Class<IndentSpan> {
        return IndentSpan::class.java
    }

}