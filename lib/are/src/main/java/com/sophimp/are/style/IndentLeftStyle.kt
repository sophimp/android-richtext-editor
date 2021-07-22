package com.sophimp.are.style

import android.text.Editable
import android.text.Layout
import android.text.Spannable
import android.text.style.AlignmentSpan
import android.widget.ImageView
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.IndentSpan

class IndentLeftStyle(editText: RichEditText) : BaseStyle<IndentSpan>(editText) {
    fun bindListenerForToolbarItemView(imageView: ImageView?) {
        imageView!!.setOnClickListener { update() }
    }

    override fun toolItemIconClick() {
        super.toolItemIconClick()
        update()
    }

    fun update() {
        val editable = mEditText.editableText
        val currentSelectionLines = Util.getAllSelectionLines(mEditText)
        if (currentSelectionLines.isEmpty()) {
            return
        }
        for (currentSelectionLine in currentSelectionLines) {
            val start: Int = currentSelectionLine.key
            val end: Int = currentSelectionLine.value
            if (start > end) {
                continue
            }
            val alignmentSpans = editable.getSpans(start, end, AlignmentSpan::class.java)
            if (alignmentSpans.isNotEmpty()) {
                val alignment = alignmentSpans[0].alignment
                if (alignment == Layout.Alignment.ALIGN_CENTER || alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                    continue
                }
            }
            val existingLMSpans: Array<IndentSpan> = editable.getSpans(
                currentSelectionLine.key,
                currentSelectionLine.value,
                IndentSpan::class.java
            )
            if (existingLMSpans.isNotEmpty()) {
                val currentIndentSpan: IndentSpan = existingLMSpans[0]
                editable.removeSpan(currentIndentSpan)
                val currentLevel: Int = currentIndentSpan.decreaseLevel()
                if (currentLevel > 0) {
                    editable.setSpan(
                        currentIndentSpan,
                        start,
                        end,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }
            }
        }

        Util.renumberAllListItemSpans(editable)
        mEditText.refresh(0)
    }

    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        start: Int,
        end: Int
    ) {
    }

    override fun insertSpan(span: IndentSpan, start: Int, end: Int) {
    }
}