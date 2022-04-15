package com.sophimp.are.style

import android.text.Editable
import android.text.Spanned
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.utils.Util
import com.sophimp.are.utils.Util.getParagraphEnd
import java.util.*
import kotlin.math.max
import kotlin.math.min

abstract class BaseCharacterStyle<E : ISpan>(editText: RichEditText) :
    BaseStyle<E>(editText) {

    var mFeature = ""

    override fun itemClickOnNonEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        handleAbsButtonClick(mEditText.selectionStart, mEditText.selectionEnd)
        return 0
    }

    override fun handleDeleteEvent(editable: Editable, epStart: Int, epEnd: Int) {
        handleDeleteAbsStyle()
    }

    override fun handleSingleParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {
        handleAbsInput(beforeSelectionStart)
    }

    override fun handleMultiParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {
        handleAbsInput(beforeSelectionStart)
    }

    protected open fun handleAbsInput(beforeSelectionStart: Int) {
        // 如果当前改变的区域
        val sEnd = mEditText.selectionEnd
        if (beforeSelectionStart < sEnd) {
            val editable = mEditText.editableText
            val targetSpans =
                editable.getSpans(max(beforeSelectionStart - 1, 0), sEnd, targetClass())
            val newSpan = newSpan()
            if (targetSpans.isNotEmpty()) {
                var lastSpan = targetSpans[0]
                var preSpanStart = editable.getSpanStart(lastSpan)
                var preSpanEnd = editable.getSpanEnd(lastSpan)
                targetSpans.forEach {
                    preSpanStart = min(editable.getSpanStart(it), preSpanStart)
                    preSpanEnd = max(editable.getSpanEnd(it), preSpanEnd)
                }
                if (newSpan != null) {
                    // 非DynamicSpan, 根据特征值判断是否重新选择了字体大小，颜色
                    removeSpans(editable, targetSpans)
                    if (!checkFeatureEqual(newSpan, targetSpans[targetSpans.size - 1])) {
                        // 前一个不一样的span
                        if (preSpanStart < beforeSelectionStart) {
                            setSpan(lastSpan, preSpanStart, beforeSelectionStart)
                        }
                        if (beforeSelectionStart < sEnd) {
                            setSpan(newSpan, beforeSelectionStart, sEnd)
                        }
                        if (sEnd < preSpanEnd) {
                            val splitSpan = newSpan(lastSpan)
                            splitSpan?.let {
                                setSpan(splitSpan, sEnd, preSpanEnd)
                            }
                        }
                    } else {
                        // 所有的合并
                        var mergeEnd = max(sEnd, preSpanEnd)
                        if (preSpanStart < mergeEnd) {
                            setSpan(lastSpan, preSpanStart, mergeEnd)
                        }
                    }
                } else {
                    // 处理非 DynamicStyle, isChecked = false
                    // 没有选中了, 设置之前的样式
                    // 前一个不一样的span
                    if (preSpanStart < beforeSelectionStart) {
                        setSpan(lastSpan, preSpanStart, beforeSelectionStart)
                    }
                    // 中间分隔开了
                    if (sEnd < preSpanEnd) {
                        val splitSpan = newSpan(lastSpan)
                        splitSpan?.let {
                            setSpan(splitSpan, sEnd, preSpanEnd)
                        }
                    }
                }
            } else {
                // 没有样式
                if (isChecked && newSpan != null) {
                    setSpan(newSpan, beforeSelectionStart, sEnd)
                    // 合并相同style
//                    mergeSameStyle(beforeSelectionStart, sEnd)
                }
            }
        }
    }

    protected open fun handleAbsButtonClick(start: Int, end: Int) {
        val editable = mEditText.editableText
        if (start < end) {
            val targetSpans = editable.getSpans(start, end, targetClass())
            val newSpan = newSpan()
            if (targetSpans.isNotEmpty()) {
                // 选择区域内有不同style的情况会导致多次设置同一个span, 统一放在最后再设置
                var hasSet = false
                // 有样式, 根据feature值来判断
                for (tar in targetSpans) {
                    val curStart = editable.getSpanStart(tar)
                    val curEnd = editable.getSpanEnd(tar)
                    // 先将原有的移除
                    editable.removeSpan(tar)
                    // 将选中区域 拆分 targetSpan
                    splitSpan(tar, curStart, curEnd, start, end)
                    // 最后再设置新的
                    if (newSpan != null && !hasSet) {
                        // 将选中区域 拆分 targetSpan
                        if (checkFeatureEqual(tar, newSpan)) {
                            // 将选中区域的 targetSpan 合并成一个
                            setSpan(tar, min(curStart, start), max(curEnd, end))
                        } else {
                            setSpan(newSpan, start, end)
                        }
                        hasSet = true
                    }
                }
            } else {
                // 没有样式
                if (isChecked && newSpan != null) {
                    setSpan(newSpan, start, end)
                }
            }
            // 合并相同style
            mergeSameStyle(start, end)
            mEditText.markChanged()
        }
    }

    override fun handleInputNewLine(
        editable: Editable,
        beforeSelectionStart: Int,
        epStart: Int,
        epEnd: Int
    ) {
        /*
            行尾有样式换行，需要分段， 如果不分段，当前段有段落样式的话(列表，缩进)，如果超过两行，格式会乱
        */
//        val lastPStart: Int = Util.getParagraphStart(mEditText, beforeSelectionStart)
//        var lastPEnd: Int = Util.getParagraphEnd(editable, beforeSelectionStart)
//        if (lastPEnd <= lastPStart) return
//        val preParagraphSpans = editable.getSpans(lastPEnd - 1, lastPEnd, targetClass())
//        if (preParagraphSpans.isEmpty()) return
//        Util.log("pre line: " + lastPStart + " - " + lastPEnd + " cur line: " + mEditText.selectionStart + " - " + mEditText.selectionEnd)
//        val preSpanStart = editable.getSpanStart(preParagraphSpans[0])
//        // 先移除上一行的span
//        removeSpans(editable, preParagraphSpans)
//        // 移除当前行的Spans
//        removeSpans(
//            editable,
//            editable.getSpans(mEditText.selectionStart, mEditText.selectionEnd, targetClass())
//        )
//        // 设计当前行span
//        val newSpan = newSpan(preParagraphSpans[preParagraphSpans.size - 1])
//        newSpan?.let {
//            if (epStart <= epEnd) {
//                setSpan(newSpan, epStart, epEnd)
//            }
//        }
//        if (preSpanStart < lastPEnd) {
//            setSpan(preParagraphSpans[0], preSpanStart, lastPEnd)
//        }
    }

    protected open fun mergeSameStyle(start: Int, end: Int) {
        // 前后紧挨着相同属性的， 同一个span在同一个区域设置多次的
        val editable = mEditText.editableText
        val targetSpans =
            editable.getSpans(max(0, start - 1), min(end + 1, mEditText.length()), targetClass())
        if (targetSpans.size < 2) return
        Arrays.sort(targetSpans) { o1: E, o2: E ->
            editable.getSpanStart(o1) - editable.getSpanStart(o2)
        }
        var i = 0
        var tStart = editable.getSpanStart(targetSpans[0])
        var tEnd = editable.getSpanEnd(targetSpans[0])
        var tarSpan: ISpan? = null
        while (i < (targetSpans.size - 1)) {
            val curEnd = editable.getSpanEnd(targetSpans[i])
            val nextStart = editable.getSpanStart(targetSpans[i + 1])
            val nextEnd = editable.getSpanEnd(targetSpans[i + 1])
            if (checkFeatureEqual(targetSpans[i], targetSpans[i + 1])) {
                if (curEnd >= nextStart) {
                    // 同一个起点, 紧挨着或者相交, 都要合并成一个
                    tStart = min(tStart, nextStart)
                    tEnd = max(tEnd, nextEnd)
                    editable.removeSpan(targetSpans[i])
                    if (i == targetSpans.size - 2) {
                        editable.removeSpan(targetSpans[i + 1])
                    }
                    tarSpan = targetSpans[i + 1]
                } else if (curEnd < nextStart) {
                    // 相离，不需要合并
                    break
                }
            } else {
                tStart = nextStart
                tEnd = nextEnd
            }
            i++
        }
        if (tarSpan != null && tStart < tEnd) {
            setSpan(tarSpan, tStart, tEnd)
        }
    }

    private fun splitSpan(tar: E, curStart: Int, curEnd: Int, start: Int, end: Int) {
        if (curStart < start) {
            setSpan(tar, curStart, start)
        }
        if (end < curEnd) {
            setSpan(newSpan(tar)!!, end, curEnd)
        }
    }

    open fun checkFeatureEqual(span1: ISpan?, span2: ISpan?): Boolean {
        return isChecked
    }

    private fun handleDeleteAbsStyle() {
        val targetSpans = mEditText.editableText.getSpans(
            mEditText.selectionStart,
            mEditText.selectionEnd,
            targetClass()
        )
        var hasDelete = false
        if (targetSpans.isNotEmpty()) {
            targetSpans.forEach {
                // 清除掉start == end 的span
                if (mEditText.editableText.getSpanStart(it) == mEditText.editableText.getSpanEnd(it)) {
                    hasDelete = true
                    mEditText.editableText.removeSpan(it)
                }
            }
        }
//        if (hasDelete) {
//            // 删除也需要触发一下状读的变换
//            mEditText.styleSelectionChanged(this)
//        }
    }

    override fun newSpan(inheritSpan: ISpan?): ISpan? {
        return if (isChecked || inheritSpan != null) targetClass().newInstance() else null
    }

    override fun setSpan(span: ISpan, start: Int, end: Int) {
        mEditText.editableText.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mEditText.isChange = true
        mEditText.refreshRange(start, end)
    }
}