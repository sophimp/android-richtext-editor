package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.SubscriptSpan2

class SubscriptStyle(
    editText: RichEditText
) : ABSStyle<SubscriptSpan2>(editText, SubscriptSpan2::class.java) {

    override fun newSpan(): SubscriptSpan2? {
        return SubscriptSpan2()
    }

}