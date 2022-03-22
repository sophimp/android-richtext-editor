package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan

/**
 *
 * @author: sfx
 * @since: 2021/7/26
 */
abstract class BaseFreeStyle<T : ISpan>(editText: RichEditText) : BaseStyle<T>(editText) {
    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {
        // 单独个体， 无需处理
    }

    override fun handleSingleParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {
    }

    override fun handleMultiParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {
    }

    override fun handleInputNewLine(
        editable: Editable,
        beforeSelectionStart: Int,
        epStart: Int,
        epEnd: Int
    ) {
    }

    override fun handleDeleteEvent(editable: Editable, epStart: Int, epEnd: Int) {}

}