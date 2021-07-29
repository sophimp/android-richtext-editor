package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.QuoteSpan2

class QuoteStyle(editText: RichEditText) : BaseListStyle<QuoteSpan2>(editText) {

    override fun newSpan(inheritSpan: ISpan?): ISpan? {
        return QuoteSpan2()
    }

    override fun targetClass(): Class<QuoteSpan2> {
        return QuoteSpan2::class.java
    }

}