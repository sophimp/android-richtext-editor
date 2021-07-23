package com.sophimp.are.style

import android.text.Spanned
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.IDynamicSpan
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
abstract class ABSDynamicStyle<E : IDynamicSpan>(
    editText: RichEditText,
    clz: Class<E>?
) : ABSStyle<E>(editText, clz!!) {
    protected var mColor: Int = 0
    protected var hasChanged = false

    override fun handleAbsInput(beforeSelectionStart: Int) {
//        super.handleAbsInput(beforeSelectionStart);
        // 如果当前改变的区域
        val editable = mEditText.editableText
        val sEnd = mEditText.selectionEnd
        if (beforeSelectionStart < sEnd) {
            val targetSpans = editable.getSpans(beforeSelectionStart, sEnd, clazzE)
            if (isChecked) {
                val newSpan: E? = newSpan()
                if (targetSpans.isNotEmpty()) {
                    // 颜色值不一样前后的span都得重新设置
                    val curSpan = targetSpans[targetSpans.size - 1]
                    val curStart = editable.getSpanStart(curSpan)
                    if (newSpan == null || curSpan.dynamicFeature != newSpan.dynamicFeature) {
                        editable.removeSpan(curSpan)
                        // 设置原有的
                        editable.setSpan(
                            curSpan,
                            curStart,
                            beforeSelectionStart,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                        if (newSpan != null) {
                            editable.setSpan(
                                newSpan,
                                beforeSelectionStart,
                                sEnd,
                                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                            )
                        }
                    }
                } else {
                    // 设置新的
                    if (hasChanged && newSpan != null) {
                        hasChanged = false
                        editable.setSpan(
                            newSpan,
                            beforeSelectionStart,
                            sEnd,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                }
            } else {
                if (targetSpans.isNotEmpty()) {
                    val curSpan = targetSpans[targetSpans.size - 1]
                    val curStart = editable.getSpanStart(curSpan)
                    if (curStart < beforeSelectionStart) {
                        editable.removeSpan(curSpan)
                        editable.setSpan(
                            curSpan,
                            curStart,
                            beforeSelectionStart,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                }
            }
            logAllABSSpans(editable, clazzE.simpleName, 0, editable.length)
        }
    }

    override fun handleAbsButtonClickStyle() {
//        super.handleAbsButtonClickStyle();
        val editable = mEditText.editableText
        val sStart = mEditText.selectionStart
        val sEnd = mEditText.selectionEnd
        if (sStart < sEnd) {
            val targetSpans = editable.getSpans(sStart, sEnd, clazzE)
            if (targetSpans.isNotEmpty()) {
                // 有样式, 根据feature值来判断
                val newSpan: E? = newSpan()
                for (tar in targetSpans) {
                    val curStart = editable.getSpanStart(tar)
                    val curEnd = editable.getSpanEnd(tar)
                    // 先将原有的移除
                    editable.removeSpan(tar)
                    if (newSpan == null) {
                        // 删除背景
                        // 将选中区域 拆分 targetSpan
                        if (curStart < sStart) {
                            editable.setSpan(
                                newSpan(tar!!.dynamicFeature),
                                curStart,
                                sStart,
                                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                            )
                        }
                        if (sEnd < curEnd) {
                            editable.removeSpan(tar)
                            editable.setSpan(
                                newSpan(tar!!.dynamicFeature),
                                sEnd,
                                curEnd,
                                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                            )
                        }
                    } else if (tar!!.dynamicFeature == newSpan.dynamicFeature) {
                        // 将选中区域的 targetSpan 合并成一个
                        editable.setSpan(
                            tar,
                            min(curStart, sStart),
                            max(curEnd, sEnd),
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    } else {
                        // 将选中区域 拆分 targetSpan
                        if (curStart < sStart) {
                            editable.setSpan(
                                newSpan(tar.dynamicFeature),
                                curStart,
                                sStart,
                                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                            )
                        }
                        if (sEnd < curEnd) {
                            editable.removeSpan(tar)
                            editable.setSpan(
                                newSpan(tar.dynamicFeature),
                                sEnd,
                                curEnd,
                                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                            )
                        }
                        // 最后再设置新的
                        editable.setSpan(newSpan, sStart, sEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    }
                }
            } else {
                // 没有样式
                if (isChecked) {
                    editable.setSpan(newSpan(), sStart, sEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                }
            }
        }
    }

    protected abstract fun featureChangedHook(feature: Int)
    protected abstract fun newSpan(feature: Int): E?
}