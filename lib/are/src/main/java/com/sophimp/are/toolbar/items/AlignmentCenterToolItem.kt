package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.AlignmentCenterSpan
import com.sophimp.are.style.AlignmentStyle

class AlignmentCenterToolItem(style: AlignmentStyle<AlignmentCenterSpan>) :
    AbstractItem<AlignmentCenterSpan>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_align_center
}