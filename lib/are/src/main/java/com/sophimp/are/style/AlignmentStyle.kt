package com.sophimp.are.style

import android.text.Editable
import android.text.Layout
import android.text.Spannable
import android.text.Spanned
import android.text.style.AlignmentSpan
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.Util.getAllSelectionLines
import com.sophimp.are.Util.getCurrentCursorLine
import com.sophimp.are.Util.getThisLineEnd
import com.sophimp.are.Util.getThisLineStart
import com.sophimp.are.spans.AlignmentSpan2

class AlignmentStyle(
    editText: RichEditText,
    alignment: Layout.Alignment
) : BaseStyle(editText) {

    private val mAlignment: Layout.Alignment = alignment

    override fun toolItemIconClick() {
        super.toolItemIconClick()
        update()
    }

    fun update() {
        val editable = mEditText.editableText
        val currentSelectionLines = getAllSelectionLines(mEditText)
        if (currentSelectionLines.isEmpty()) {
            val currentLine = getCurrentCursorLine(mEditText)
            val start = getThisLineStart(mEditText, currentLine)
            var end = getThisLineEnd(mEditText, currentLine)
            val alignmentSpans =
                editable.getSpans(
                    start,
                    end,
                    AlignmentSpan2::class.java
                )
            removeSpans(editable, alignmentSpans)
            val alignCenterSpan = AlignmentSpan.Standard(mAlignment)
            if (start == end) {
                editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
                end = getThisLineEnd(mEditText, currentLine)
            }
            editable.setSpan(alignCenterSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            return
        }
        for ((start, end) in currentSelectionLines) {
            val alignmentSpans =
                editable.getSpans(
                    start,
                    end,
                    AlignmentSpan2::class.java
                )
            removeSpans(editable, alignmentSpans)
            val alignCenterSpan = AlignmentSpan.Standard(mAlignment)
            editable.setSpan(alignCenterSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }

    private fun markLineAsAlignmentSpan(alignment: Layout.Alignment) {
        val editText = mEditText
        val currentLine = getCurrentCursorLine(editText)
        var start = getThisLineStart(editText, currentLine)
        val editable = editText.editableText
        editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
        start = getThisLineStart(editText, currentLine)
        var end = getThisLineEnd(editText, currentLine)
        if (end < 1) {
            return
        }
        if (editable[end - 1] == Constants.CHAR_NEW_LINE) {
            end--
        }
        val alignmentSpan: AlignmentSpan = AlignmentSpan.Standard(alignment)
        if (start <= end) {
            editable.setSpan(alignmentSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }

    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        start: Int,
        end: Int
    ) {
        val alignmentSpans = editable.getSpans(start, end, AlignmentSpan::class.java)
        if (null == alignmentSpans || alignmentSpans.isEmpty()) {
            return
        }
        val alignment = alignmentSpans[0].alignment
        if (mAlignment != alignment) {
            return
        }
        if (end > start) {
            //
            // User inputs
            //
            // To handle the \n case
            val c = editable[end - 1]
            if (c == Constants.CHAR_NEW_LINE) {
                val alignmentSpansSize = alignmentSpans.size
                val previousAlignmentSpanIndex = alignmentSpansSize - 1
                if (previousAlignmentSpanIndex > -1) {
                    val previousAlignmentSpan =
                        alignmentSpans[previousAlignmentSpanIndex]
                    val lastAlignmentSpanStartPos =
                        editable.getSpanStart(previousAlignmentSpan)
                    if (end > lastAlignmentSpanStartPos) {
                        editable.removeSpan(previousAlignmentSpan)
                        editable.setSpan(
                            previousAlignmentSpan,
                            lastAlignmentSpanStartPos,
                            end - 1,
                            Spanned.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }
                    markLineAsAlignmentSpan(mAlignment)
                }
            } // #End of user types \n
        } else {
            //
            // User deletes
            val spanStart = editable.getSpanStart(alignmentSpans[0])
            val spanEnd = editable.getSpanEnd(alignmentSpans[0])
            if (spanStart >= spanEnd) {
                //
                // User deletes the last char of the span
                // So we think he wants to remove the span
                editable.removeSpan(alignmentSpans[0])

                //
                // To delete the previous span's \n
                // So the focus will go to the end of previous span
                if (spanStart > 0) {
                    editable.delete(spanStart - 1, spanEnd)
                }
            }
        }
    }

}