package com.sophimp.are.style

import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.Util.getParagraphEnd
import com.sophimp.are.Util.getParagraphStart
import com.sophimp.are.spans.HrSpan
import com.sophimp.are.spans.IListSpan
import com.sophimp.are.spans.IndentSpan

class HrStyle(mEditText: RichEditText) : BaseStyle(mEditText) {
    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        start: Int,
        end: Int
    ) {
    }

    override fun toolItemIconClick() {
        val editable = mEditText.editableText
        val start = mEditText.selectionStart
        val end = mEditText.selectionEnd
        val pStart = getParagraphStart(mEditText, start)
        val pEnd = getParagraphEnd(editable, end)
        var lStart = -1
        var dStart = -1
        val listSpans =
            editable.getSpans(pStart, pEnd, IListSpan::class.java)
        val leadingMarginSpans =
            editable.getSpans(pStart, pEnd, IndentSpan::class.java)
        // 记录首段的样式
        if (listSpans.isNotEmpty()) {
            lStart = editable.getSpanStart(listSpans[0])
        }
        if (leadingMarginSpans.isNotEmpty()) {
            dStart = editable.getSpanStart(leadingMarginSpans[0])
        }
        removeSpans(editable, listSpans)
        removeSpans(editable, leadingMarginSpans)
        mEditText.stopMonitor()
        val ssb = SpannableStringBuilder()
        ssb.append(Constants.CHAR_NEW_LINE)
        ssb.append(Constants.ZERO_WIDTH_SPACE_STR)
        ssb.append(Constants.CHAR_NEW_LINE)
        ssb.setSpan(HrSpan(context), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        editable.replace(start, end, ssb)
        if (lStart >= 0 && lStart == pStart && lStart < start) {
            editable.setSpan(listSpans[0], lStart, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (dStart >= 0 && dStart == pStart && dStart < start) {
            editable.setSpan(
                leadingMarginSpans[0],
                dStart,
                start,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        mEditText.startMonitor()
    }
}