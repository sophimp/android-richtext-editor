package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.SuperscriptSpan2

/**
 *
 * @author: sfx
 * @since: 2021/7/22
 */
class SuperscriptStyle(editText: RichEditText) :
    ABSStyle<SuperscriptSpan2>(editText, SuperscriptSpan2::class.java) {

    override fun newSpan(): SuperscriptSpan2? {
        return SuperscriptSpan2()
    }

}