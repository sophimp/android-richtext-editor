package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.UnderlineSpan2

class UnderlineStyle(editText: RichEditText) :
    ABSStyle<UnderlineSpan2>(editText, UnderlineSpan2::class.java) {

    override fun newSpan(): UnderlineSpan2? {
        return UnderlineSpan2()
    }

}