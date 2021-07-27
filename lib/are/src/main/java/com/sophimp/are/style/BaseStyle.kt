package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.BuildConfig
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.IndentSpan
import java.util.*
import kotlin.math.max

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
abstract class BaseStyle<TA : ISpan>(protected var curEditText: RichEditText) : IStyle {
    protected var context = curEditText.context
    protected var checkState: Boolean = false

    override fun bindEditText(editText: RichEditText) {
        curEditText = editText
    }

    protected fun <FT : ISpan> removeSpans(editable: Editable, spans: Array<FT>) {
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
                index += curPEnd + off + 1
                continue
            }

            off = itemClickOnNonEmptyParagraph(curPStart, curPEnd)

            spEnd += off
            index += curPEnd + off + 1
        }
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

    protected fun <T : ISpan> updateSpan(spans: Array<T>, start: Int, end: Int, update: Boolean) {
        removeSpans(mEditText.editableText, spans)
        if (spans.isNotEmpty() && update) {
            setSpan(spans[0], start, end)
        } else {
            val ns = newSpan()
            if (ns != null) {
                setSpan(ns, start, end)
            }
        }
    }

    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int
    ) {
        when (event) {
            IStyle.TextEvent.DELETE -> handleDeleteEvent(editable)
            IStyle.TextEvent.INPUT_NEW_LINE -> handleInputNewLine(editable, beforeSelectionStart)
            IStyle.TextEvent.INPUT_SINGLE_PARAGRAPH -> handleSingleParagraphInput(
                editable,
                changedText,
                beforeSelectionStart,
                afterSelectionEnd
            )
            IStyle.TextEvent.INPUT_MULTI_PARAGRAPH -> handleMultiParagraphInput(
                editable,
                changedText,
                beforeSelectionStart,
                afterSelectionEnd
            )
        }
    }

    abstract fun handleMultiParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int
    )


    abstract fun handleSingleParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int
    )


    abstract fun handleInputNewLine(editable: Editable, beforeSelectionStart: Int)

    abstract fun handleDeleteEvent(editable: Editable)

    /**
     * 每个 style 处理的span
     */
    abstract fun targetClass(): Class<TA>

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
        val listItemSpans = editable.getSpans(start, end, targetClass())
        // 坑点， 这里取出来的span 并不是按先后顺序， 需要先排序
        Arrays.sort(
            listItemSpans
        ) { o1: TA, o2: TA ->
            editable.getSpanStart(o1) - editable.getSpanStart(o2)
        }
        val leadingMarginSpans: Array<IndentSpan> =
            editable.getSpans(start, end, IndentSpan::class.java)
        Util.log("-----------$tag--------------")
        for (span in listItemSpans) {
            val ss = editable.getSpanStart(span)
            val se = editable.getSpanEnd(span)
            Util.log("List All " + targetClass().simpleName + ": " + " :: start == " + ss + ", end == " + se)
        }
        for (span in leadingMarginSpans) {
            val ss = editable.getSpanStart(span)
            val se = editable.getSpanEnd(span)
            Util.log("List All leading span:  :: start == $ss, end == $se")
        }
        Util.log(tag + " : " + "总长度: " + editable.length)
    }

    override fun newSpan(): ISpan? {
        return null
    }
}