package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ItalicSpan

class ItalicStyle(editText: RichEditText) :
    ABSStyle<ItalicSpan>(editText, ItalicSpan::class.java) {

    override fun newSpan(): ItalicSpan? {
        return ItalicSpan()
    }

}