package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
interface IStyle {
    enum class TextEvent {
        IDLE,

        /**
         * cut or del
         */
        DELETE,

        /**
         * multi paragraph input or paste
         */
        INPUT_MULTI_PARAGRAPH,

        /**
         * single paragraph input or paste
         */
        INPUT_SINGLE_PARAGRAPH,

        /**
         * new line input
         */
        INPUT_NEW_LINE
    }

    /**
     * current bind RichEditText
     */
    val mEditText: RichEditText

    /**
     * current style checked state
     */
    val isChecked: Boolean

    /**
     * apply the style to change at start and end
     */
    fun applyStyle(
        editable: Editable,
        event: TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    )

    /**
     * create newSpan
     */
    fun newSpan(inheritSpan: ISpan? = null): ISpan?

    fun bindEditText(editText: RichEditText) {}

    /**
     * insertSpan
     */
    fun setSpan(span: ISpan, start: Int, end: Int) {}

    /**
     * toolbar item click handle
     */
    fun toolItemIconClick() {}
}