package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.StrikeThroughSpan2

class StrikethroughStyle(editText: RichEditText) :
    ABSStyle<StrikeThroughSpan2>(editText, StrikeThroughSpan2::class.java) {

    override fun newSpan(): StrikeThroughSpan2 {
        return StrikeThroughSpan2()
    }

}