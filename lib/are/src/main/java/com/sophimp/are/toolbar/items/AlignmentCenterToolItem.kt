package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.AlignmentCenterStyle

class AlignmentCenterToolItem(style: AlignmentCenterStyle) :
    AbstractItem(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_align_center
}