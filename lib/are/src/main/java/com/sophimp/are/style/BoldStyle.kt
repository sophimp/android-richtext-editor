package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.BoldSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
class BoldStyle(editText: RichEditText) :
    ABSStyle<BoldSpan>(editText, BoldSpan::class.java) {

    override fun newSpan(): BoldSpan? {
        return BoldSpan()
    }

}