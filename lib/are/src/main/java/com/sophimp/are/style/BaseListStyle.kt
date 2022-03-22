package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.IListSpan
import com.sophimp.are.utils.Util

/**
 * Abstract for ListBullet, ListTodo, ListNumber
 * @author: sfx
 * @since: 2021/7/22
 */
abstract class BaseListStyle<B : IListSpan>(editText: RichEditText) : BaseParagraphStyle<B>(editText) {

    /**
     * 每次插入一个span, 可能会多插入一个字符，使用off记录
     */
    private var off = 0
    private var isEmptyLine = false

    override fun itemClickOnEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        off = 0
        isEmptyLine = true
        addTargetStyle(curPStart, curPEnd)
        mEditText.refreshRange(curPStart, curPEnd)
        return off
    }

    override fun itemClickOnNonEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        /*
            case 1:  target style -> no style
            . aa            . aa
            . bb   ->       bb
            . cc            . cc
         */
        /*
            case 2:  empty style -> target style
            aa            . aa
            bb   ->       . bb
            cc            . cc
         */
        /*
            case 3: other list style -> target style
            1. aa            1. aa
            2. bb   ->       .bb
            3. cc            1. cc
         */
        off = 0
        val editable = mEditText.editableText
        val basicSpans = editable.getSpans(curPStart, curPEnd, targetClass())
        if (basicSpans != null && basicSpans.isNotEmpty()) {
            // case 1:  target style -> no style
            clearTargetStyle(basicSpans)
        } else {
            val otherListSpans = editable.getSpans(curPStart, curPEnd, IListSpan::class.java)
            if (otherListSpans.isNotEmpty()) {
                // case 3: other list style -> target style
                otherStyle2TargetStyle(otherListSpans)
            } else {
                // case 2:  empty style -> target style
                addTargetStyle(curPStart, curPEnd)
            }
        }
        mEditText.refreshRange(curPStart, curPEnd)
        return off
    }

    override fun toolItemIconClick() {
        super.toolItemIconClick()

    }

    /**
     * case 2:  empty style -> target style
     */
    private fun addTargetStyle(start: Int, end: Int) {
        val editable = mEditText.editableText
        val nSpan = newSpan() ?: return
        if (start == end) {
            // empty line
            editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
            off = 1
        }
        setSpan(nSpan, start, end + off)
    }

    /**
     * case 3: other list style -> target style
     */
    private fun otherStyle2TargetStyle(spans: Array<IListSpan>) {
        val editable = mEditText.editableText
        val start = editable.getSpanStart(spans[0])
        val end = editable.getSpanEnd(spans[0])
        editable.removeSpan(spans[0])
        addTargetStyle(start, end)
    }

    /**
     * case 1:  target style -> no style
     */
    private fun clearTargetStyle(spans: Array<B>) {
        val editable = mEditText.editableText
        removeSpans(editable, spans)
    }

    override fun handleInputNewLine(
        editable: Editable,
        beforeSelectionStart: Int,
        epStart: Int,
        epEnd: Int
    ) {
        super.handleInputNewLine(editable, beforeSelectionStart, epStart, epEnd)
        // 重排所有的 ListNumberSpan
        val tarSpans = editable.getSpans(epStart, epEnd, targetClass());
        if (tarSpans.isNotEmpty()) {
            Util.renumberAllListItemSpans(editable)
//        logAllSpans(editable, targetClass().simpleName + " after new line", 0, editable.length)
        }
    }

    override fun handleMultiParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {
        super.handleMultiParagraphInput(editable, changedText, beforeSelectionStart, afterSelectionEnd, epStart, epEnd)

        val tarSpans = editable.getSpans(epStart, epEnd, targetClass());
        if (tarSpans.isNotEmpty()) {
            Util.renumberAllListItemSpans(editable)
        }
    }

    override fun removeMutexSpans(curPStart: Int, curPEnd: Int) {
        val listSpans = mEditText.editableText.getSpans(curPStart, curPEnd, IListSpan::class.java)
        removeSpans(mEditText.editableText, listSpans)
    }

    /**
     * 处理删除事件
     */
    override fun handleDeleteEvent(editable: Editable, epStart: Int, epEnd: Int) {
        super.handleDeleteEvent(editable, epStart, epEnd)
        // 重排所有的 ListNumberSpan, 因为数据量并不会大， 所在重排的性能损失可以忽略，但是实现方法简单得多
        val tarSpans = editable.getSpans(epStart, epEnd, targetClass());
        if (tarSpans.isNotEmpty()) {
            Util.renumberAllListItemSpans(editable)
        }
    }


}