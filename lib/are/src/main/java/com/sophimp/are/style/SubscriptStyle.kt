package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.SubscriptSpan2

class SubscriptStyle(
    editText: RichEditText
) : BaseCharacterStyle<SubscriptSpan2>(editText) {

    override fun newSpan(inheritSpan: ISpan?): SubscriptSpan2? {
        return SubscriptSpan2()
    }

    override fun targetClass(): Class<SubscriptSpan2> {
        return SubscriptSpan2::class.java
    }

}