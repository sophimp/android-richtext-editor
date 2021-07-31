package com.sophimp.are.style

import android.text.Editable
import android.text.TextUtils
import com.sophimp.are.BuildConfig
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.ISpan
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
abstract class BaseStyle<T : ISpan>(private var curEditText: RichEditText) : IStyle {
    protected var context = curEditText.context
    protected var checkState: Boolean = false

    override fun bindEditText(editText: RichEditText) {
        curEditText = editText
    }

    protected fun <FT : Any> removeSpans(editable: Editable, spans: Array<FT>) {
        if (spans.isNotEmpty()) {
            for (span in spans) {
                editable.removeSpan(span)
            }
        }
    }

    /**
     * default handle by paragraph style
     * the absolute style override this function in {@link com.sophimp.are.style.ABSStyle}
     */
    override fun toolItemIconClick() {
        checkState = !checkState
        mEditText.isChange = true
        val editable = mEditText.editableText
        val spStart = Util.getParagraphStart(mEditText, mEditText.selectionStart)
        var spEnd = Util.getParagraphEnd(editable, mEditText.selectionEnd)
        var index = max(0, spStart)
        var off = 0
        while (index <= spEnd) {
            val curPStart = index
            var curPEnd: Int = Util.getParagraphEnd(editable, index)
            if (curPEnd == editable.length - 1) {
                // 最后一段换行符读不到
                curPEnd = editable.length
            }
            Util.log("sgx cake currentStart - end:$curPStart-$curPEnd")
            if (curPStart > curPEnd) {
                // 这种情况理论上不存在， 但是之前的段落首尾算法有误， 后面修改就没有这种情况， 为了防止死循环，加上保险一点, 影响不了多少性能
                index += 1
                continue
            } else if (curPStart == curPEnd) {
                // 空行
                off = itemClickOnEmptyParagraph(curPStart, curPEnd)
                spEnd += off
                index = curPEnd + off + 1
                continue
            }

            off = itemClickOnNonEmptyParagraph(curPStart, curPEnd)

            spEnd += off
            index = curPEnd + off + 1
        }
        logAllSpans(mEditText.editableText, "${targetClass().simpleName} item click", 0, mEditText.editableText.length)
    }

    /**
     * @return 若有新增字符，需返回偏移量
     */
    open fun itemClickOnNonEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        return 0
    }

    /**
     * @return 若有新增字符，需返回偏移量
     */
    open fun itemClickOnEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        return 0
    }

    open fun <T : ISpan> updateSpan(spans: Array<T>, start: Int, end: Int) {
        if (spans.isNotEmpty()) {
            removeSpans(mEditText.editableText, spans)
            setSpan(spans[0], start, end)
        } else {
            val ns = newSpan(null)
            if (ns != null) {
                setSpan(ns, start, end)
            }
        }
    }

    /**
     * @param epStart effect paragraph start selection
     * @param epEnd effect paragraph end selection
     */
    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {
        when (event) {
            IStyle.TextEvent.DELETE -> handleDeleteEvent(editable, epStart, epEnd)
            IStyle.TextEvent.INPUT_NEW_LINE -> handleInputNewLine(editable, beforeSelectionStart)
            IStyle.TextEvent.INPUT_SINGLE_PARAGRAPH -> handleSingleParagraphInput(
                editable,
                changedText,
                beforeSelectionStart,
                afterSelectionEnd,
                epStart,
                epEnd
            )
            IStyle.TextEvent.INPUT_MULTI_PARAGRAPH -> handleMultiParagraphInput(
                editable,
                changedText,
                beforeSelectionStart,
                afterSelectionEnd,
                epStart,
                epEnd

            )
        }
        logAllSpans(editable, "base apply style: ${this.javaClass.simpleName}", 0, editable.length)
    }

    /**
     * @param epStart effect paragraph start selection
     * @param epEnd effect paragraph end selection
     */
    abstract fun handleMultiParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    )

    /**
     * @param epStart effect paragraph start selection
     * @param epEnd effect paragraph end selection
     */
    abstract fun handleSingleParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    )


    open fun handleInputNewLine(editable: Editable, beforeSelectionStart: Int) {
        /*
            case 1: 有 内容 换行
                移除换行前的span, 然后在当前行与前一行分别添加baseClassSpan即可
            case 2: 没有内容换行
                取消当前的baseClassSpan, 同时移除缩进
         */
        // start 换到当前行的上一行的末尾
        val lastPStart: Int = Util.getParagraphStart(mEditText, beforeSelectionStart)
        var lastPEnd: Int = Util.getParagraphEnd(editable, beforeSelectionStart)
        if (lastPEnd < lastPStart) lastPEnd = lastPStart
        val preParagraphSpans = editable.getSpans(lastPStart, lastPEnd, targetClass())
        if (preParagraphSpans.isEmpty()) return
        Util.log("sgx cake: 上一行: " + lastPStart + " - " + lastPEnd + " 当前行: " + mEditText.selectionStart + " - " + mEditText.selectionEnd)
        // 先移除上一行的span
        removeSpans(editable, preParagraphSpans)
        // 移除当前行的Spans
        removeSpans(editable, editable.getSpans(mEditText.selectionStart, mEditText.selectionEnd, targetClass()))

        // 再将上一行与当前行统一处理
        val lastContent = editable.subSequence(lastPStart, lastPEnd).toString()
        if (TextUtils.isEmpty(lastContent) || lastContent.length == 1 && lastContent[0].toInt() == Constants.ZERO_WIDTH_SPACE_INT) {
            // case 2: 没有内容换行
            editable.delete(max(0, mEditText.selectionStart - 1), mEditText.selectionStart)
        } else {
            // case 1: 有内容换行,
            var nSpan = newSpan()
            if (preParagraphSpans.isNotEmpty()) {
                // 前一行添加span
                setSpan(preParagraphSpans[0], lastPStart, lastPEnd)

                nSpan = newSpan(preParagraphSpans[0])
                // 当前行添加span
                if (nSpan != null) {
                    val curStart = lastPEnd + 1
                    if (curStart >= editable.length || editable[curStart].toInt() != Constants.ZERO_WIDTH_SPACE_INT) {
                        editable.insert(curStart, Constants.ZERO_WIDTH_SPACE_STR)
                    }
                    setSpan(nSpan, curStart, min(curStart + 1, editable.length))
                }
            }
        }
    }

    abstract fun handleDeleteEvent(editable: Editable, epStart: Int, epEnd: Int)

    /**
     * 每个 style 处理的span
     */
    abstract fun targetClass(): Class<T>

    override val isChecked: Boolean
        get() = checkState

    override val mEditText: RichEditText
        get() = curEditText

    protected fun logAllSpans(
        editable: Editable,
        tag: String,
        start: Int,
        end: Int
    ) {
        if (!BuildConfig.DEBUG) return
        val targets = editable.getSpans(start, end, targetClass())
        // 坑点， 这里取出来的span 并不是按先后顺序， 需要先排序
        Arrays.sort(targets) { o1: T, o2: T ->
            editable.getSpanStart(o1) - editable.getSpanStart(o2)
        }
        Util.log("-----------$tag--------------")
        for (span in targets) {
            val ss = editable.getSpanStart(span)
            val se = editable.getSpanEnd(span)
            Util.log(targetClass().simpleName + ": start == " + ss + ", end == " + se)
        }
    }

    override fun newSpan(inheritSpan: ISpan?): ISpan? {
        return null
    }
}