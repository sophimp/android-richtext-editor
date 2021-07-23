package com.sophimp.are.style

import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.QuoteSpan2

class QuoteStyle(editText: RichEditText) : BaseStyle(editText) {
    private var mRemovedNewLine = false

    override fun itemClickOnNonEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        if (isChecked) {
            makeLineAsQuote()
        } else {
            removeQuote()
        }
        return 0
    }

    /**
     * @return
     */
    private fun makeLineAsQuote() {
        val editText = mEditText
        val currentLine: Int = Util.getCurrentCursorLine(editText)
        var start: Int = Util.getThisLineStart(editText, currentLine)
        var end: Int
        val editable = editText.editableText
        if (editable.length > start) {
            if (editable[start].toInt() != Constants.ZERO_WIDTH_SPACE_INT) {
                editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
            }
        } else {
            editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
        }
        start = Util.getThisLineStart(editText, currentLine)
        end = Util.getThisLineEnd(editText, currentLine)
        if (editable[end - 1] == Constants.CHAR_NEW_LINE) {
            end--
        }
        var existingQuoteSpans: Array<QuoteSpan2> =
            editable.getSpans(start, end, QuoteSpan2::class.java)
        if (existingQuoteSpans.isNotEmpty()) {
            return
        }
        if (start > 2) {
            existingQuoteSpans = editable.getSpans(start - 2, start, QuoteSpan2::class.java)
            if (existingQuoteSpans.isNotEmpty()) {
                // Merge forward
                val quoteStart = editable.getSpanStart(existingQuoteSpans[0])
                editable.setSpan(
                    existingQuoteSpans[0],
                    quoteStart,
                    end,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
                return
            }
        }
        val quoteSpan = QuoteSpan2()
        editable.setSpan(
            quoteSpan, start, end,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    private fun removeQuote() {
        val editText = mEditText
        val editable = editText.editableText
        val currentLine: Int = Util.getCurrentCursorLine(editText)
        val start: Int = Util.getThisLineStart(editText, currentLine)
        val end: Int = Util.getThisLineEnd(editText, currentLine)
        var quoteSpans: Array<QuoteSpan2>
        if (start == 0) {
            quoteSpans = editable.getSpans(start, end, QuoteSpan2::class.java)
            editable.removeSpan(quoteSpans[0])
            return
        } else {
            quoteSpans = editable.getSpans(start - 1, end, QuoteSpan2::class.java)
        }
        if (quoteSpans.isEmpty()) {
            quoteSpans = editable.getSpans(start, end, QuoteSpan2::class.java)
            if (quoteSpans.isEmpty()) {
                editable.removeSpan(quoteSpans[0])
                return
            }
        }
        val quoteStart = editable.getSpanStart(quoteSpans!![0])
        editable.removeSpan(quoteSpans[0])
        if (start > quoteStart) {
            editable.setSpan(
                quoteSpans[0],
                quoteStart,
                start - 1,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
    }

    fun applyStyle(editable: Editable, start: Int, end: Int) {
//        Util.log("Quote apply style, start == " + start + ", end == " + end + ", is quote checked == " + mQuoteChecked);
//        if (!mQuoteChecked) {
//            return;
//        }
        val quoteSpans: Array<QuoteSpan2> =
            editable.getSpans(start, end, QuoteSpan2::class.java)
        if (null == quoteSpans || quoteSpans.size == 0) {
            return
        }

        // Handle \n and backspace
        if (end > start) {
            // User inputs
            val c = editable[end - 1]
            if (c == Constants.CHAR_NEW_LINE) {
                editable.append(Constants.ZERO_WIDTH_SPACE_STR)
            }
        } else {
            // User deletes
            val quoteSpan: QuoteSpan2 = quoteSpans[0]
            val spanStart = editable.getSpanStart(quoteSpan)
            val spanEnd = editable.getSpanEnd(quoteSpan)
            Util.log("Delete spanStart = $spanStart  spanEnd = $spanEnd start == $start")
            if (spanStart == spanEnd) {
                removeQuote()
            }
            if (end > 2) {
                if (mRemovedNewLine) {
                    mRemovedNewLine = false
                    return
                }
                val pChar = editable[end - 1]
                if (pChar == Constants.CHAR_NEW_LINE) {
                    //
                    // This case
                    // |aa
                    // |
                    // When user deletes at the first of the 2nd line (i.e.: ZERO_WIDTH_STR)
                    // We want to put cursor to the end of the previous line "aa"
                    mRemovedNewLine = true
                    editable.delete(end - 1, end)
                }
            }
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
    }

}