package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
abstract class BaseStyle<T : ISpan>(protected var curEditText: RichEditText) : IStyle<T> {
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

    override fun toolItemIconClick() {
        checkState = !checkState
    }

    override val isChecked: Boolean
        get() = checkState

    override val mEditText: RichEditText
        get() = curEditText

}