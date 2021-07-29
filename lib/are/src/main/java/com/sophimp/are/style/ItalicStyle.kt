package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.ItalicSpan

class ItalicStyle(editText: RichEditText) :
    BaseCharacterStyle<ItalicSpan>(editText) {

    override fun newSpan(inheritSpan: ISpan?): ItalicSpan? {
        return ItalicSpan()
    }

    override fun targetClass(): Class<ItalicSpan> {
        return ItalicSpan::class.java
    }

}