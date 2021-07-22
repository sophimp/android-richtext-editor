package com.sophimp.are.style

import android.text.Editable
import android.text.Spanned
import android.widget.EditText
import com.sophimp.are.BuildConfig
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.LineSpaceSpan
import java.util.*
import kotlin.math.abs

/**
 * @des: 行距控制
 * @since: 2021/6/15
 * @version: 0.1f
 * @author: sfx
 */
class LineSpaceStyle(editText: RichEditText, protected var isLarge: Boolean) :
    BaseStyle<LineSpaceSpan>(editText) {
    private val EPSILON = 1e-5

    override fun toolItemIconClick() {
        super.toolItemIconClick()
        val editText = mEditText
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        /*
         * 1. 根据光标选中的起始-终点位置，计算出跨了哪些段落起始点
         * 2. 再依据所跨段落的起始点， 算出跨了哪些段落, 及每个段落的起始点
         * 3. 依次设置段落 LineSpaceSpan
         * 4. 转换成html，再根据将每一段起始-终点间的换行符都去掉
         * 5. 反显html时， 需要使用StaticLayout 测量一遍，手动添加每一行的换行符。
         */
        val start: Int = Util.getParagraphStart(editText, selectionStart)
        val end: Int = Util.getParagraphEnd(editText.editableText, selectionEnd)
        val editable = editText.editableText

        Util.log("sgx line start-end: " + start + "-" + end + " extra: " + editText.lineSpacingExtra)
        if (start > end) return
        var index = start
        while (index < end) {
            val curPEnd: Int = Util.getParagraphEnd(editable, index)
            if (index < curPEnd) {
                updateLineSpaceSpan(editText, index, curPEnd)
                index = curPEnd
            } else {
                index += 1
            }
        }
        mEditText.isChange = true
    }

    private fun logLineSpaceSpanItems(editable: Editable) {
        if (!BuildConfig.DEBUG) return
        val lineSpaceSpans = editable.getSpans(0, editable.length, LineSpaceSpan::class.java)
        Util.log("-------------------------")
        for (span in lineSpaceSpans) {
            val ss = editable.getSpanStart(span)
            val se = editable.getSpanEnd(span)
            Util.log("sgx List All " + span.javaClass.simpleName + ": " + " :: start == " + ss + ", end == " + se)
        }
    }

    private fun updateLineSpaceSpan(text: EditText, start: Int, end: Int) {
        val editable = text.editableText
        // 移除旧有的span
        var factor = 1.0f
        val lineSpaceSpans =
            editable.getSpans(start, end, LineSpaceSpan::class.java)
        if (lineSpaceSpans.size > 0) {
            factor = lineSpaceSpans[0].factor
        }
        factor = if (isLarge) {
            if (factor >= 5.5) {
                Util.toast(context, "无法增大了")
                return
            }
            // 增大行距
            getNextLargeFactor(factor)
        } else {
            if (factor <= 1.0) {
                Util.toast(context, "无法缩小了")
                return
            }
            getNextSmallFactor(factor)
        }
        Util.toast(context, factor.toString() + "倍")
        // 先移除已有的
        for (lineSpaceSpan in lineSpaceSpans) {
            editable.removeSpan(lineSpaceSpan)
        }
        val spaceSpan = LineSpaceSpan(factor, text.lineHeight.toFloat())
        editable.setSpan(spaceSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        logLineSpaceSpanItems(editable)
        mEditText.refresh(start)
    }

    private fun getNextSmallFactor(factor: Float): Float {
        when {
            abs(factor - 5.5f) < EPSILON -> {
                return 4.0f
            }
            abs(factor - 4f) < EPSILON -> {
                return 3.0f
            }
            abs(factor - 3f) < EPSILON -> {
                return 2.0f
            }
            abs(factor - 2f) < EPSILON -> {
                return 1.5f
            }
            abs(factor - 1.5f) < EPSILON -> {
                return 1.25f
            }
            abs(factor - 1.25f) < EPSILON -> {
                return 1f
            }
            abs(factor - 1f) < EPSILON -> {
                return 1f
            }
            else -> return 1f
        }
    }

    private fun getNextLargeFactor(factor: Float): Float {
        when {
            abs(factor - 1f) < EPSILON -> {
                return 1.25f
            }
            abs(factor - 1.25f) < EPSILON -> {
                return 1.5f
            }
            abs(factor - 1.5f) < EPSILON -> {
                return 2.0f
            }
            abs(factor - 2f) < EPSILON -> {
                return 3.0f
            }
            abs(factor - 3f) < EPSILON -> {
                return 4.0f
            }
            abs(factor - 4f) < EPSILON -> {
                return 5.5f
            }
            abs(factor - 5.5f) < EPSILON -> {
                return 5.5f
            }
            else -> return 1f
        }
    }

    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        start: Int,
        end: Int
    ) {
        when (event) {
            IStyle.TextEvent.DELETE -> handleDeleteLineSpace(editable, start)
            IStyle.TextEvent.INPUT_NEW_LINE -> handleLineSpacingNewLine(
                editable,
                beforeSelectionStart
            )
        }
    }

    private fun handleDeleteLineSpace(editable: Editable, start: Int) {
        val pStart: Int = Util.getParagraphStart(mEditText, start)
        val pEnd: Int = Util.getParagraphEnd(editable, start)
        val lineSpaceSpans =
            editable.getSpans(pStart, pEnd, LineSpaceSpan::class.java)
        Arrays.sort(
            lineSpaceSpans
        ) { o1: LineSpaceSpan?, o2: LineSpaceSpan? ->
            editable.getSpanStart(o1) - editable.getSpanStart(o2)
        }
        removeSpans(editable, lineSpaceSpans)
        if (pStart < pEnd && lineSpaceSpans.isNotEmpty()) {
            editable.setSpan(lineSpaceSpans[0], pStart, pEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun handleLineSpacingNewLine(
        editable: Editable,
        beforeSelectionStart: Int
    ) {
        // 如果是从段中换行， 需要将LineSpaceSpan 拆分
        val lineSpaceSpans = editable.getSpans(
            beforeSelectionStart,
            mEditText.selectionEnd,
            LineSpaceSpan::class.java
        )
        if (lineSpaceSpans.isNotEmpty()) {
            val span = lineSpaceSpans[lineSpaceSpans.size - 1]
            val lStart = editable.getSpanStart(span)
            val lEnd = editable.getSpanEnd(span)
            removeSpans(editable, lineSpaceSpans)
            if (lStart < beforeSelectionStart) {
                editable.setSpan(
                    span,
                    lStart,
                    beforeSelectionStart,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (mEditText.selectionEnd < lEnd) {
                editable.setSpan(
                    LineSpaceSpan(span.factor, (-1).toFloat()),
                    mEditText.selectionEnd,
                    lEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    override fun insertSpan(span: LineSpaceSpan, start: Int, end: Int) {

    }
}