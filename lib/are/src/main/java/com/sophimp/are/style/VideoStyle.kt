package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.VideoSpan

class VideoStyle(editText: RichEditText) : BaseFreeStyle<VideoSpan>(editText) {

    override fun targetClass(): Class<VideoSpan> {
        return VideoSpan::class.java
    }

}