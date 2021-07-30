package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.AlignmentCenterStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class AlignmentCenterToolItem(style: AlignmentCenterStyle, itemClickAction: IToolbarItemClickAction? = null) :
    AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_align_center
}