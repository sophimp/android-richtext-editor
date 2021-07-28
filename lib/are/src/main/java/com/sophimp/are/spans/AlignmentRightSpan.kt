package com.sophimp.are.spans

import android.text.Layout
import android.text.style.AlignmentSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
class AlignmentRightSpan : AlignmentSpan, ISpan {
    override fun getAlignment(): Layout.Alignment {
        return Layout.Alignment.ALIGN_OPPOSITE
    }
}