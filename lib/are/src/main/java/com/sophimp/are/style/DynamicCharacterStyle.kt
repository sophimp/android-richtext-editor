package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.IDynamicSpan
import com.sophimp.are.spans.ISpan
import kotlin.math.max
import kotlin.math.min

/**
 * Dynamic style abstract implementation.
 *
 *
 * Dynamic means the Span has a configurable value which can provide different features.
 * Such as: Font color / Font size.
 *
 * @param <E>
</E> */
abstract class DynamicCharacterStyle<E : IDynamicSpan>(editText: RichEditText) :
    BaseCharacterStyle<E>(editText) {
    protected var mColor: Int = 0
    protected var hasChanged = false

    override fun handleAbsInput(beforeSelectionStart: Int) {
//        super.handleAbsInput(beforeSelectionStart);
        // 如果当前改变的区域
        val editable = mEditText.editableText
        val sEnd = mEditText.selectionEnd
        if (beforeSelectionStart < sEnd) {
            val targetSpans = editable.getSpans(beforeSelectionStart, sEnd, targetClass())
            if (isChecked) {
                val newSpan = newSpan() as IDynamicSpan
                if (targetSpans.isNotEmpty()) {
                    // 颜色值不一样前后的span都得重新设置
                    val curSpan = targetSpans[targetSpans.size - 1]
                    val curStart = editable.getSpanStart(curSpan)
                    if (curSpan.dynamicFeature != newSpan.dynamicFeature) {
                        editable.removeSpan(curSpan)
                        // 设置原有的
                        setSpan(curSpan, curStart, beforeSelectionStart)
                        setSpan(newSpan, beforeSelectionStart, sEnd)
                    }
                } else {
                    // 设置新的
                    if (hasChanged) {
                        hasChanged = false
                        setSpan(newSpan, beforeSelectionStart, sEnd)
                    }
                }
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

    override fun handleAbsButtonClick() {
//        super.handleAbsButtonClickStyle();
        val editable = mEditText.editableText
        val sStart = mEditText.selectionStart
        val sEnd = mEditText.selectionEnd
        if (sStart < sEnd) {
            val targetSpans = editable.getSpans(sStart, sEnd, targetClass())
            if (targetSpans.isNotEmpty()) {
                // 有样式, 根据feature值来判断
                val newSpan = newSpan() as IDynamicSpan
                for (tar in targetSpans) {
                    val curStart = editable.getSpanStart(tar)
                    val curEnd = editable.getSpanEnd(tar)
                    // 先将原有的移除
                    editable.removeSpan(tar)
                    if (tar!!.dynamicFeature == newSpan.dynamicFeature) {
                        // 将选中区域的 targetSpan 合并成一个
                        setSpan(tar, min(curStart, sStart), max(curEnd, sEnd))
                    } else {
                        // 将选中区域 拆分 targetSpan
                        if (curStart < sStart) {
                            setSpan(newSpan(tar.dynamicFeature) as ISpan, curStart, sStart)
                        }
                        if (sEnd < curEnd) {
                            editable.removeSpan(tar)
                            setSpan(
                                newSpan(tar.dynamicFeature) as ISpan, sEnd, curEnd
                            )
                        }
                        // 最后再设置新的
                        setSpan(newSpan, sStart, sEnd)
                    }
                }
            } else {
                // 没有样式
                if (isChecked) {
                    setSpan(newSpan() as ISpan, sStart, sEnd)
                }
            }
        }
    }

    protected abstract fun featureChangedHook(feature: Int)
    protected abstract fun newSpan(feature: Int): E?
}