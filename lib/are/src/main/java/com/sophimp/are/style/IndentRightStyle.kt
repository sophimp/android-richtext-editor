package com.sophimp.are.style

import android.text.Editable
import android.text.Layout
import android.text.Spannable
import android.text.style.AlignmentSpan
import android.text.style.CharacterStyle
import android.text.style.ParagraphStyle
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.IListSpan
import com.sophimp.are.spans.IndentSpan
import java.util.*

class IndentRightStyle(editText: RichEditText) : BaseStyle(editText) {

    override fun toolItemIconClick() {
        super.toolItemIconClick()

        Util.renumberAllListItemSpans(mEditText.editableText)
        mEditText.setSelection(mEditText.selectionStart, mEditText.selectionEnd)
        mEditText.refresh(0)
    }

    override fun itemClickOnNonEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        update()
        return 0
    }

    fun update() {
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
            val alignmentSpans =
                editable.getSpans(start, end, AlignmentSpan::class.java)
            if (null != alignmentSpans && alignmentSpans.isNotEmpty()) {
                val alignment = alignmentSpans[0].alignment
                if (alignment == Layout.Alignment.ALIGN_CENTER || alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                    continue
                }
            }
            val existingLeadingSpans: Array<IndentSpan> =
                editable.getSpans(start, end, IndentSpan::class.java)
            for (span in existingLeadingSpans) {
                Util.log(
                    "sgx cake existingLeadingSpans: " + editable.getSpanStart(span) + "-" + editable.getSpanEnd(
                        span
                    )
                )
            }
            if (existingLeadingSpans.isNotEmpty()) {
                val currentLeadingMarginSpan: IndentSpan = existingLeadingSpans[0]
                if (currentLeadingMarginSpan.level >= IndentSpan.MAX_LEVEL) {
                    Util.toast(context, "每行最多缩进" + IndentSpan.MAX_LEVEL.toString() + "次")
                    continue
                }
                editable.removeSpan(currentLeadingMarginSpan)
                currentLeadingMarginSpan.increaseLevel()
                editable.setSpan(
                    currentLeadingMarginSpan,
                    start,
                    end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            } else {
                val leadingMarginSpan = IndentSpan()
                leadingMarginSpan.increaseLevel()
                editable.setSpan(leadingMarginSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }

    }

    fun applyStyle(editable: Editable, start: Int, end: Int) {
        // 处理 剩余所有span 的移除
        if (start == end) {
            val css =
                editable.getSpans(start, end, CharacterStyle::class.java)
            if (css != null) {
                for (span in css) {
                    val s = editable.getSpanStart(span)
                    val e = editable.getSpanEnd(span)
                    if (s == e) {
                        editable.removeSpan(span)
                    }
                    //                    else if(s + 1 == e){
//                        if (editable.charAt(s) == Constants.ZERO_WIDTH_SPACE_INT){
//                            editable.removeSpan(span);
//                        }
//                    }
                }
            }
            val pss = editable.getSpans(start, end, ParagraphStyle::class.java)
            if (pss != null) {
                for (span in pss) {
                    val s = editable.getSpanStart(span)
                    val e = editable.getSpanEnd(span)
                    if (s == e) {
                        if (span !is IListSpan) {
                            // DRListSpan 仍需要自己来删除, 需要用其判断是否换行
                            editable.removeSpan(span)
                        } else {
                            // 全选删除的临界情况
                            if (editable.getSpanStart(span) == 0) {
                                editable.removeSpan(span)
                            }
                        }
                    }
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