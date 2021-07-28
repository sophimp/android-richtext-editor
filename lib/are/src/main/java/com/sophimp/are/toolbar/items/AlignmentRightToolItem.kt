package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.AlignmentRightStyle

class AlignmentRightToolItem(style: AlignmentRightStyle) :
    AbstractItem(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_align_right

}