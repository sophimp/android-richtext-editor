package com.sophimp.are.style

import android.text.Editable
import android.text.Spanned
import android.text.TextUtils
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.IndentSpan
import kotlin.math.max
import kotlin.math.min

/**
 *
 * @author: sfx
 * @since: 2021/7/26
 */
abstract class BaseParagraphStyle<T : ISpan>(editText: RichEditText) : BaseStyle<T>(editText) {
    override fun handleMultiParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int
    ) {
        val changedEnd: Int = afterSelectionEnd
        val effectPStart: Int = Util.getParagraphStart(mEditText, beforeSelectionStart)
        val effectPEnd: Int = Util.getParagraphEnd(editable, changedEnd)
        val effectFirstPEnd: Int = Util.getParagraphEnd(editable, beforeSelectionStart)
        val firstTargetParagraphSpans: Array<T> = editable.getSpans(effectPStart, effectFirstPEnd, targetClass())
//        val firstPLeadingSpans: Array<IndentSpan> = editable.getSpans(effectPStart, effectFirstPEnd, IndentSpan::class.java)
        if (firstTargetParagraphSpans.isEmpty()) return
        val allTargetParagraphSpans: Array<T> = editable.getSpans(effectPStart, effectPEnd, targetClass())
//        val allPLeadSpans: Array<IndentSpan> = editable.getSpans(effectPStart, effectPEnd, IndentSpan::class.java)
        // 先移复制内容部分所有的缩进与
        removeSpans(editable, allTargetParagraphSpans)
//        removeSpans(editable, allPLeadSpans)
        logAllSpans(editable, "多行输入前处理", 0, editable.length)
        // 暂时都使用同级类目处理
        handleCommonInput(editable, effectPStart, effectPEnd, firstTargetParagraphSpans)
        logAllSpans(editable, javaClass.simpleName + " apply style 后", 0, editable.length)
    }

    /**
     * 非富文本的多段复制
     */
    private fun handleCommonInput(
        editable: Editable,
        effectPStart: Int,
        effectPEnd: Int,
        firstPListSpans: Array<T>
    ) {
        var index = effectPStart
        var off = 0
        while (index < effectPEnd) {
            off = 0
            var pEnd: Int = Util.getParagraphEnd(editable, index)
            if (pEnd < 0) {
                // 最后一段
                pEnd = effectPEnd
            }
            if (index < pEnd) {
                val nSpan = newSpan()
                if (firstPListSpans.isNotEmpty() && nSpan != null) {
                    try {
                        setSpan(nSpan, index, pEnd)
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InstantiationException) {
                        e.printStackTrace()
                    }
                }
            } // else { // 相等， 空行, 不处理
            index = pEnd + 1 + off
        }
    }

    override fun handleSingleParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int
    ) {
        // 由于是 SPAN_EXCLUSIVE_EXCLUSIVE， 格式需要设置到整个段落，否则光标会错位
        // 所以每次输入完后，需要重新设置一下span为整个段落, 这里由于只是对一段的处理，性能影响O(n)，n为段落长度，可以忽略
        val pStart: Int = Util.getParagraphStart(mEditText, beforeSelectionStart)
        val pEnd: Int = Util.getParagraphEnd(editable, afterSelectionEnd)
        if (pStart < pEnd) {
            val base = editable.getSpans(pStart, pEnd, targetClass())
            if (base.isNotEmpty()) {
                removeSpans(editable, base)
                setSpan(base[0], pStart, pEnd)
                mEditText.refresh(beforeSelectionStart)
            }
        }
    }

    override fun handleInputNewLine(editable: Editable, beforeSelectionStart: Int) {
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
        Util.log("sgx cake: 上一行: " + lastPStart + " - " + lastPEnd + " 当前行: " + mEditText.getSelectionStart() + " - " + mEditText.getSelectionEnd())
        val preParagraphSpans: Array<T> = editable.getSpans(lastPStart, lastPEnd, targetClass())
        if (preParagraphSpans.isEmpty()) return
        // 先移除上一行的span
        removeSpans(editable, preParagraphSpans)
        // 移除当前行的Spans
        removeSpans(
            editable,
            editable.getSpans(
                mEditText.selectionStart,
                mEditText.selectionEnd,
                targetClass()
            )
        )
        removeSpans(
            editable,
            editable.getSpans(
                mEditText.selectionStart,
                mEditText.selectionEnd,
                IndentSpan::class.java
            )
        )

        // 再将上一行与当前行统一处理
        val lastContent = editable.subSequence(lastPStart, lastPEnd).toString()
        if (TextUtils.isEmpty(lastContent) || lastContent.length == 1 && lastContent[0].toInt() == Constants.ZERO_WIDTH_SPACE_INT) {
            // case 2: 没有内容换行
            editable.delete(
                max(0, mEditText.selectionStart - 1),
                mEditText.selectionStart
            )
            //            getEditText().setSelection(Math.max(0, getEditText().getSelectionStart() - 1));
        } else {
            // case 1: 有内容换行,
            // 前一行添加span
            if (preParagraphSpans.isNotEmpty()) {
                setSpan(preParagraphSpans[0], lastPStart, lastPEnd)
            }
            // 当前行添加span
            val nSpan = newSpan()
            if (preParagraphSpans.isNotEmpty() && nSpan != null) {
                try {
                    val curStart: Int = mEditText.selectionStart
                    editable.insert(curStart, Constants.ZERO_WIDTH_SPACE_STR)
                    setSpan(nSpan, curStart, min(curStart + 1, editable.length))
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                }
            }
        }

    }

    override fun handleDeleteEvent(editable: Editable) {
        /*
               删除操作
               case 1: 空行换上一行, 需要删除当前行的span
               case 2: 带内容删除换上一行, 需要删除当前行span
               case 3: 带内容，直接从行首删除换上一行, 需要合并内容
               case 4: 不换行, 正常删除
             */

        // 不管哪一种删除，只要将删除后的当前光标段落, 再重新设置一下Span即可
        val curPStart: Int = Util.getParagraphStart(mEditText, mEditText.selectionStart)
        val curPEnd: Int = Util.getParagraphEnd(editable, mEditText.selectionStart)
        val preDelListSpans: Array<T> = editable.getSpans(curPStart, max(mEditText.selectionStart - 1, curPStart), targetClass())
        val preLeadSpans: Array<IndentSpan> = editable.getSpans(
            curPStart,
            max(mEditText.selectionStart - 1, curPStart),
            IndentSpan::class.java
        )
        val curListSpans: Array<T> = editable.getSpans(curPStart, curPEnd, targetClass())
        val curLeadingSpans: Array<IndentSpan> =
            editable.getSpans(curPStart, curPEnd, IndentSpan::class.java)
        if (curListSpans.isEmpty() && curLeadingSpans.isEmpty()) return
        logAllSpans(editable, "即将删除的spans ", curPStart, curPEnd)
        removeSpans(editable, curListSpans)
        removeSpans(editable, curLeadingSpans)
        val nSpan = newSpan()
        if (preDelListSpans.isNotEmpty() && nSpan != null) {
            try {
                setSpan(nSpan, curPStart, curPEnd)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            }
        }
        if (preLeadSpans.isNotEmpty()) {
            setSpan(IndentSpan(preLeadSpans[0].mLevel), curPStart, curPEnd)
        }

        logAllSpans(editable, javaClass.simpleName + " apply delete 后", 0, editable.length)
    }

    override fun setSpan(span: ISpan, start: Int, end: Int) {
        mEditText.editableText.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

}