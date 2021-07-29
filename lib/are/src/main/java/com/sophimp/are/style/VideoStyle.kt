package com.sophimp.are.style

import android.text.Editable
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.VideoSpan

class VideoStyle(editText: RichEditText) : BaseFreeStyle<VideoSpan>(editText) {
    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int,
        epStart: Int,
        epEnd: Int
    ) {
    }

    override fun targetClass(): Class<VideoSpan> {
        return VideoSpan::class.java
    }

}