package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.BoldSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
class BoldStyle(editText: RichEditText) :
    BaseCharacterStyle<BoldSpan>(editText) {

    override fun targetClass(): Class<BoldSpan> {
        return BoldSpan::class.java
    }

}