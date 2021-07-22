package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.RichEditText

class VideoStyle(editText: RichEditText) : BaseStyle(editText) {
    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        start: Int,
        end: Int
    ) {
    }

}