package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.StrikethroughSpan2

class StrikethroughStyle(editText: RichEditText) :
    BaseCharacterStyle<StrikethroughSpan2>(editText) {

    override fun targetClass(): Class<StrikethroughSpan2> {
        return StrikethroughSpan2::class.java
    }

}