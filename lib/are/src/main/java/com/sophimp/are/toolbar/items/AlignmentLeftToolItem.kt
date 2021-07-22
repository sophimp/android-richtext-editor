package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.AlignmentLeftSpan
import com.sophimp.are.style.AlignmentStyle

class AlignmentLeftToolItem(style: AlignmentStyle<AlignmentLeftSpan>) :
    AbstractItem<AlignmentLeftSpan>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_align_left

}