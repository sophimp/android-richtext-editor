package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.VideoSpan

class VideoStyle(editText: RichEditText) : BaseStyle<VideoSpan>(editText) {
    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        start: Int,
        end: Int
    ) {
    }

    override fun insertSpan(span: VideoSpan, start: Int, end: Int) {
    }

}