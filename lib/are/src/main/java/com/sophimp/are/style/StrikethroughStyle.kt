package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.StrikeThroughSpan2

class StrikethroughStyle(editText: RichEditText) :
    BaseCharacterStyle<StrikeThroughSpan2>(editText) {

    override fun targetClass(): Class<StrikeThroughSpan2> {
        return StrikeThroughSpan2::class.java
    }

}