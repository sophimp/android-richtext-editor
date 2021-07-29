package com.sophimp.are.style

import android.text.Editable
import android.text.Spanned
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.IListSpan
import com.sophimp.are.spans.ISpan

/**
 * Abstract for ListBullet, ListTodo, ListNumber
 * @author: sfx
 * @since: 2021/7/22
 */
abstract class BaseListStyle<B : IListSpan, T : IListSpan, TT : IListSpan>(
    editText: RichEditText,
    protected var basicClass: Class<B>,
    protected var targetClass1: Class<T>,
    protected var targetClass2: Class<TT>
) : BaseParagraphStyle<B>(editText) {

    /**
     * 每次插入一个span, 可能会多插入一个字符，使用off记录
     */
    private var off = 0
    private var isEmptyLine = false

    override fun itemClickOnEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        isEmptyLine = true
        handleClickCase2(curPStart, curPEnd + 1)
        return off
    }

    override fun itemClickOnNonEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        // 每一种case 的变化只针对ListBulletSpan, 保留 DRLeadingSpan, Alignment
        /*
            case 1: ListBulletSpan 有->无
            . aa            . aa
            . bb   ->       bb
            . cc            . cc
         */
        /*
            case 2: ListBulletSpan 无->有
            aa            . aa
            bb   ->       . bb
            cc            . cc
            特例： 空行 -> 有
         */
        /*
            case 3: ListNumberSpan -> ListBulletSpan
            1. aa            1. aa
            2. bb   ->       .bb
            3. cc            1. cc
            替换逻辑同case 2, 选中的下一段如果是 ListNumberSpan 需要重新排序
         */
        /*
            case 4: DRTodoSpan -> ListBulletSpan
            O aa            O aa
            O bb   ->       .bb
            O cc            O cc
            替换逻辑同case 2
         */
        val editable = mEditText.editableText
        val basicSpans =
            editable.getSpans(curPStart, curPEnd, basicClass)
        val targetSpans1 =
            editable.getSpans(curPStart, curPEnd, targetClass1)
        val targetSpans2 =
            editable.getSpans(curPStart, curPEnd, targetClass2)
        if (basicSpans != null && basicSpans.isNotEmpty()) {
            // case 1: 有 -> 无
            handleClickCase1(basicSpans)
        } else {
            if (targetSpans1 != null && targetSpans1.isNotEmpty()) {
                // case 3: TargetSpan1 -> BasicSpan, 每一行的转换
                handleClickCase3(targetSpans1)
            } else if (targetSpans2 != null && targetSpans2.isNotEmpty()) {
                // case 4: TargetSpan2 -> BasicSpan, 每一行的转换
                handleClickCase4(targetSpans2)
            } else {
                // case 2: 无 -> 有
                handleClickCase2(curPStart, curPEnd)
            }
        }
        return off
    }

    override fun toolItemIconClick() {
        super.toolItemIconClick()

        // 重排上一段落及后面所有的 ListNumberSpan, 因为数据量并不会大， 所在重排的性能损失可以忽略，但是实现方法简单得多
        mEditText.postDelayUIRun(Runnable {
            Util.renumberAllListItemSpans(mEditText.editableText)
            mEditText.refresh(0)
            logAllSpans(mEditText.editableText, "${targetClass().simpleName} item click", 0, mEditText.editableText.length)
        }, 30)
    }

    private fun handleClickCase2(start: Int, end: Int) {
//        LogUtils.d("sgx cake setSpan start-end: " + currentStart + " - " + currentEnd);
        var currentStart = start
        val editable = mEditText.editableText
        if (editable != null) {
            if (editable.isEmpty()) {
                // 全为空
                currentStart = 0
                editable.append(Constants.ZERO_WIDTH_SPACE_STR)
                off = 1
            } else if (currentStart >= editable.length) {
                // 最后一行行尾
                editable.append(Constants.ZERO_WIDTH_SPACE_STR)
                off = 1
            } else if (currentStart == end && editable[currentStart] == '\n') {
                editable.insert(currentStart, Constants.ZERO_WIDTH_SPACE_STR)
                off = 1
            } else {
                if (editable[currentStart].toInt() != Constants.ZERO_WIDTH_SPACE_INT) {
                    // 每个ListBullet 插入一个零字符， 用于删除时处理最一个字符的临界情况
                    editable.insert(currentStart, Constants.ZERO_WIDTH_SPACE_STR)
                    off = 1
                }
            }
            val nSpan = newSpan() ?: return
            if (isEmptyLine) {
                // 针对空行情况, 第一次添加，处理不了那么快
                mEditText.postDelayUIRun(Runnable {
                    try {
                        isEmptyLine = false
                        setSpan(nSpan, currentStart, currentStart + 1)
                        mEditText.refresh(0)
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InstantiationException) {
                        e.printStackTrace()
                    }
                }, 0)
            } else {
                try {
                    setSpan(nSpan, currentStart, end)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                }
                mEditText.refresh(0)
            }
        }
        //        logAllSpans(editable, "case2 添加span后");
    }

    /**
     * case 4: TargetSpan2 -> BasicSpan, 每一行的转换
     */
    private fun handleClickCase4(spans: Array<TT>) {
        val editable = mEditText.editableText
        val start = editable.getSpanStart(spans[0])
        val end = editable.getSpanEnd(spans[0])
        editable.removeSpan(spans[0])
        handleClickCase2(start, end)
    }

    /**
     * case 3: TargetSpan1 -> BasicSpan, 每一行的转换
     */
    private fun handleClickCase3(spans: Array<T>) {
        val editable = mEditText.editableText
        val start = editable.getSpanStart(spans[0])
        val end = editable.getSpanEnd(spans[0])
        editable.removeSpan(spans[0])
        handleClickCase2(start, end)
    }

    /**
     * case 1 : 有 -> 无
     */
    private fun handleClickCase1(spans: Array<B>) {
        val editable = mEditText.editableText
        removeSpans(editable, spans)
    }

    /**
     * 处理换行操作
     */
    override fun handleInputNewLine(editable: Editable, beforeSelectionStart: Int) {
        super.handleInputNewLine(editable, beforeSelectionStart)
        // 重排所有的 ListNumberSpan, 因为数据量并不会大， 所在重排的性能损失可以忽略，但是实现方法简单得多
        Util.renumberAllListItemSpans(editable)
        mEditText.refresh(0)
        logAllSpans(editable, targetClass().simpleName + "after new line", 0, editable.length)
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
        Util.renumberAllListItemSpans(editable)
        mEditText.refresh(0)
    }

    /**
     * 处理删除事件
     */
    override fun handleDeleteEvent(editable: Editable, epStart: Int, epEnd: Int) {
        super.handleDeleteEvent(editable, epStart, epEnd)
        // 重排所有的 ListNumberSpan, 因为数据量并不会大， 所在重排的性能损失可以忽略，但是实现方法简单得多
        Util.renumberAllListItemSpans(editable)
        mEditText.refresh(0)
    }


    override fun setSpan(span: ISpan, start: Int, end: Int) {
        mEditText.editableText.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}