package com.sophimp.are.spans

import android.text.Layout

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
class AlignmentRightSpan : AlignmentSpan2() {
    override fun getAlignment(): Layout.Alignment {
        return Layout.Alignment.ALIGN_OPPOSITE
    }
}