package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.UnderlineSpan2

class UnderlineStyle(editText: RichEditText) :
    BaseCharacterStyle<UnderlineSpan2>(editText) {

    override fun targetClass(): Class<UnderlineSpan2> {
        return UnderlineSpan2::class.java
    }

}