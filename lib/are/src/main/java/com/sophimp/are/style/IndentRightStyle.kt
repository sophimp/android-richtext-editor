package com.sophimp.are.style

import android.text.Layout
import android.text.style.AlignmentSpan
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.IndentSpan
import java.util.*

class IndentRightStyle(editText: RichEditText) : BaseParagraphStyle<IndentSpan>(editText) {

    override fun itemClickOnNonEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        handleItemClick()
        Util.renumberAllListItemSpans(mEditText.editableText)
        mEditText.setSelection(mEditText.selectionStart, mEditText.selectionEnd)
        mEditText.refresh(0)
        return 0
    }

    private fun handleItemClick() {
        val editable = mEditText.editableText
        val currentSelectionLines: List<AbstractMap.SimpleEntry<Int, Int>> =
            Util.getAllSelectionLines(mEditText)
        if (currentSelectionLines.isEmpty()) {
            return
        }
        for ((start, end) in currentSelectionLines) {
            if (start > end) {
                continue
            }
            val alignmentSpans = editable.getSpans(start, end, AlignmentSpan::class.java)
            if (null != alignmentSpans && alignmentSpans.isNotEmpty()) {
                val alignment = alignmentSpans[0].alignment
                if (alignment == Layout.Alignment.ALIGN_CENTER || alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                    continue
                }
            }
            val existingLeadingSpans: Array<IndentSpan> =
                editable.getSpans(start, end, IndentSpan::class.java)
            for (span in existingLeadingSpans) {
                Util.log("sgx cake existingLeadingSpans: ${editable.getSpanStart(span)} - ${editable.getSpanEnd(span)}")
            }
            if (existingLeadingSpans.isNotEmpty()) {
                val currentLeadingMarginSpan: IndentSpan = existingLeadingSpans[0]
                if (currentLeadingMarginSpan.level >= IndentSpan.MAX_LEVEL) {
                    Util.toast(context, "每行最多缩进" + IndentSpan.MAX_LEVEL.toString() + "次")
                    continue
                }
                editable.removeSpan(currentLeadingMarginSpan)
                currentLeadingMarginSpan.increaseLevel()
                setSpan(currentLeadingMarginSpan, start, end)
            } else {
                val leadingMarginSpan = IndentSpan()
                leadingMarginSpan.increaseLevel()
                setSpan(leadingMarginSpan, start, end)
            }
        }

    }

    override fun newSpan(): ISpan? {
        return IndentSpan(1)
    }

    override fun targetClass(): Class<IndentSpan> {
        return IndentSpan::class.java
    }

}