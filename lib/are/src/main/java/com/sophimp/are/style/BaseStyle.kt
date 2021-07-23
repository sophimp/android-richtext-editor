package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.ISpan
import kotlin.math.max

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
abstract class BaseStyle(protected var curEditText: RichEditText) : IStyle {
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


    override val isChecked: Boolean
        get() = checkState

    override val mEditText: RichEditText
        get() = curEditText

}