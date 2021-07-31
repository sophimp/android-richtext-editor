package com.sophimp.are.style

import android.text.Editable
import android.text.Spanned
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.ISpan
import java.util.*

/**
 *
 * @author: sfx
 * @since: 2021/7/26
 */
abstract class BaseParagraphStyle<T : ISpan>(editText: RichEditText) : BaseStyle<T>(editText) {

    override fun itemClickOnNonEmptyParagraph(curPStart: Int, curPEnd: Int): Int {

        removeMutexSpans(curPStart, curPEnd)

        val targets = mEditText.editableText.getSpans(curPStart, curPEnd, targetClass())
        Arrays.sort(targets) { o1: T, o2: T ->
            mEditText.editableText.getSpanStart(o1) - mEditText.editableText.getSpanStart(o2)
        }
        updateSpan(targets, curPStart, curPEnd)
        mEditText.refresh(0)
        return 0
    }

    open fun removeMutexSpans(curPStart: Int, curPEnd: Int) {}

    override fun handleMultiParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {
        val effectFirstPEnd: Int = Util.getParagraphEnd(editable, beforeSelectionStart)
        val firstTargetParagraphSpans: Array<T> = editable.getSpans(epStart, effectFirstPEnd, targetClass())
//        val firstPLeadingSpans: Array<IndentSpan> = editable.getSpans(effectPStart, effectFirstPEnd, IndentSpan::class.java)
        if (firstTargetParagraphSpans.isEmpty()) return
        val allTargetParagraphSpans: Array<T> = editable.getSpans(epStart, epEnd, targetClass())
//        val allPLeadSpans: Array<IndentSpan> = editable.getSpans(effectPStart, effectPEnd, IndentSpan::class.java)
        removeSpans(editable, allTargetParagraphSpans)
//        removeSpans(editable, allPLeadSpans)
        logAllSpans(editable, "多行输入前处理", 0, editable.length)
        // 暂时都使用同级类目处理
        handleCommonInput(editable, epStart, epEnd, firstTargetParagraphSpans)
//        logAllSpans(editable, javaClass.simpleName + " apply style 后", 0, editable.length)
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
                    setSpan(nSpan, index, pEnd)
                }
            } // else { // 相等， 空行, 不处理
            index = pEnd + 1 + off
        }
    }

    override fun handleSingleParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {
        // 由于是 SPAN_EXCLUSIVE_EXCLUSIVE， 格式需要设置到整个段落，否则光标会错位
        // 所以每次输入完后，需要重新设置一下span为整个段落, 这里由于只是对一段的处理，性能影响O(n)，n为段落长度，可以忽略
        if (epStart < epEnd) {
            val base = editable.getSpans(epStart, epEnd, targetClass())
            if (base.isNotEmpty()) {
                removeSpans(editable, base)
                setSpan(base[0], epStart, epEnd)
            }
        }
    }

    override fun handleDeleteEvent(editable: Editable, epStart: Int, epEnd: Int) {
        /*
               删除操作
               case 1: 空行换上一行, 需要删除当前行的span
               case 2: 带内容删除换上一行, 需要删除当前行span
               case 3: 带内容，直接从行首删除换上一行, 需要合并内容
               case 4: 不换行, 正常删除
             */

        // 不管哪一种删除，只要将删除后的当前光标段落, 再重新设置一下Span即可
//        val curPStart: Int = Util.getParagraphStart(mEditText, mEditText.selectionStart)
//        val curPEnd: Int = Util.getParagraphEnd(editable, mEditText.selectionStart)
        val curTargetSpans: Array<T> = editable.getSpans(epStart, epEnd, targetClass())
        Arrays.sort(curTargetSpans) { o1: T, o2: T ->
            editable.getSpanStart(o1) - editable.getSpanStart(o2)
        }
        if (curTargetSpans.isEmpty()) return
//        logAllSpans(editable, "即将删除的spans ", epStart, epEnd)
        removeSpans(editable, curTargetSpans)
        setSpan(curTargetSpans[0], epStart, epEnd)
    }

    override fun setSpan(span: ISpan, start: Int, end: Int) {
        mEditText.editableText.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

}