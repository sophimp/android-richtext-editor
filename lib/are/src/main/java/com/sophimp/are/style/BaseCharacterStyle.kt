package com.sophimp.are.style

import android.text.Editable
import android.text.Spanned
import com.sophimp.are.RichEditText
import com.sophimp.are.Util.getParagraphEnd
import com.sophimp.are.spans.ISpan
import kotlin.math.max
import kotlin.math.min

abstract class BaseCharacterStyle<E : ISpan>(editText: RichEditText) :
    BaseStyle<E>(editText) {

    override fun toolItemIconClick() {
        checkState = !checkState
        mEditText.isChange = true
        handleAbsButtonClick()
        logAllSpans(mEditText.editableText, "${targetClass().simpleName} item click", 0, mEditText.editableText.length)
    }

    override fun handleDeleteEvent(editable: Editable) {
        // 移除掉 start == end 的span即可, 其他的交由TextView处理
        val editable = mEditText.editableText
        val pEnd = getParagraphEnd(editable, mEditText.selectionEnd)
        val targetSpans = editable.getSpans(mEditText.selectionStart, pEnd, targetClass())
        for (span in targetSpans) {
            val s = editable.getSpanStart(span)
            val e = editable.getSpanEnd(span)
            if (s == e) {
                editable.removeSpan(span)
            }
        }
    }

    override fun handleSingleParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int
    ) {
        handleAbsInput(beforeSelectionStart)
    }

    override fun handleMultiParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int
    ) {
        handleAbsInput(beforeSelectionStart)
    }

    override fun handleInputNewLine(editable: Editable, beforeSelectionStart: Int) {

    }

    protected open fun handleAbsInput(beforeSelectionStart: Int) {
        // 如果当前改变的区域
        val editable = mEditText.editableText
        val sEnd = mEditText.selectionEnd
        if (beforeSelectionStart < sEnd) {
            val targetSpans = editable.getSpans(beforeSelectionStart, sEnd, targetClass())
            if (isChecked) {
                val nSpan = newSpan() ?: return
                if (targetSpans.isEmpty()) {
                    setSpan(nSpan, beforeSelectionStart, sEnd)
                } // else include特性即可
            } else {
                if (targetSpans.isNotEmpty()) {
                    val curSpan = targetSpans[targetSpans.size - 1]
                    val curStart = editable.getSpanStart(curSpan)
                    if (curStart < beforeSelectionStart) {
                        editable.removeSpan(curSpan)
                        setSpan(curSpan, curStart, beforeSelectionStart)
                    }
                }
            }
        }
    }

    protected open fun handleAbsButtonClick() {
        val editable = mEditText.editableText
        val sStart = mEditText.selectionStart
        val sEnd = mEditText.selectionEnd
        if (sStart < sEnd) {
            val targetSpans = editable.getSpans(sStart, sEnd, targetClass())
            if (targetSpans.isNotEmpty()) {
                // 有样式
                if (isChecked) {
                    // 将选中区域的targetSpan 合并成一个
                    var earlyStart = editable.getSpanStart(targetSpans[0])
                    var lastEnd = editable.getSpanEnd(targetSpans[0])
                    for (span in targetSpans) {
                        earlyStart = min(editable.getSpanStart(span), earlyStart)
                        lastEnd = max(editable.getSpanEnd(span), lastEnd)
                        editable.removeSpan(span)
                    }
                    if (earlyStart < lastEnd) {
                        setSpan(targetSpans[0], earlyStart, lastEnd)
                    }
                } else {
                    // 将选中区域的targetSpan移除， 原有的span 拆分首尾
                    val nSpan = newSpan() ?: return
                    for (span in targetSpans) {
                        val curStart = editable.getSpanStart(span)
                        val curEnd = editable.getSpanEnd(span)
                        if (curStart < sStart) {
                            setSpan(nSpan, curStart, sStart)
                        }
                        if (sEnd < curEnd) {
                            setSpan(nSpan, sEnd, curEnd)
                        }
                        editable.removeSpan(span)
                    }
                }
            } else {
                // 没有样式
                val nSpan = newSpan() ?: return
                for (span in targetSpans) {
                    if (isChecked)
                        setSpan(nSpan, sStart, sEnd)
                }
            }
        }
    }

    private fun handleDeleteAbsStyle() {

    }

    override fun setSpan(span: ISpan, start: Int, end: Int) {
        mEditText.editableText.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
    }
}