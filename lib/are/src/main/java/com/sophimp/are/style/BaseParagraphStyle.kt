package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan

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
    }

    override fun handleSingleParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int
    ) {
    }

    override fun handleInputNewLine(editable: Editable, beforeSelectionStart: Int) {
    }

    override fun handleDeleteEvent(editable: Editable) {
    }
}