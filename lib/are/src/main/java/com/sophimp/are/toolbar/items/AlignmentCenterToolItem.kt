package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.AlignmentCenterStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class AlignmentCenterToolItem(style: AlignmentCenterStyle, itemClickAction: IToolbarItemClickAction? = null) :
    AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_align_center

    override fun iconClickHandle() {
        super.iconClickHandle()
        iconView.setIconResId(if (style.isChecked) R.mipmap.icon_toolitem_align_center_sel else R.mipmap.icon_toolitem_align_center)
    }
}