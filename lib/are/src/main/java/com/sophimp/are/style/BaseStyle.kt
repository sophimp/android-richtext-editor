package com.sophimp.are.style

import android.os.Handler
import android.os.Looper
import android.text.Editable
import com.sophimp.are.BuildConfig
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.ListNumberSpan
import com.sophimp.are.utils.Util
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.max

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
abstract class BaseStyle<T : ISpan>(private var curEditText: RichEditText) : IStyle {
    protected var context = curEditText.context
    protected var checkState: Boolean = false

    companion object {
        val uiHandler = Handler(Looper.getMainLooper())
    }

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
        val editable = mEditText.editableText
        val spStart = Util.getParagraphStart(mEditText, mEditText.selectionStart)
        var spEnd = Util.getParagraphEnd(editable, mEditText.selectionEnd)
        var index = max(0, spStart)
        var off = 0
        var shouldResort = (targetClass() == ListNumberSpan::class.java)
        while (index <= spEnd) {
            val curPStart = index
            var curPEnd: Int = Util.getParagraphEnd(editable, index)
            if (curPEnd == editable.length - 1) {
                // 最后一段换行符读不到
                curPEnd = editable.length
            }
            Util.log("currentStart - end:$curPStart-$curPEnd")
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

            if (!shouldResort) {
                val numberSpans = editable.getSpans(curPStart, curPEnd, ListNumberSpan::class.java)
                shouldResort = numberSpans.isNotEmpty()
            }

            off = itemClickOnNonEmptyParagraph(curPStart, curPEnd)

            spEnd += off
            index = curPEnd + off + 1
        }
        if (shouldResort) {
            // 重排所有的 ListNumberSpan
            MainScope().launch {
                val job = async {
                    Util.renumberAllListItemSpans(mEditText.editableText)
//                    logAllSpans(
//                        mEditText.editableText,
//                        "${targetClass().simpleName} item click",
//                        0,
//                        mEditText.editableText.length
//                    )
                    mEditText.refreshRange(0, mEditText.length())
                }
//            job.await()
            }
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
            IStyle.TextEvent.INPUT_NEW_LINE -> handleInputNewLine(editable, beforeSelectionStart, epStart, epEnd)
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


    abstract fun handleInputNewLine(
        editable: Editable,
        beforeSelectionStart: Int,
        epStart: Int,
        epEnd: Int
    )

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

    override fun onSelectionChanged(selectionEnd: Int) {
        val start = max(0, selectionEnd - 1)
        val boldSpans = mEditText.editableText.getSpans(start, selectionEnd, targetClass())
        checkState = boldSpans.isNotEmpty()
    }

    override fun newSpan(inheritSpan: ISpan?): ISpan? {
        return targetClass().newInstance()
    }
}