package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.AlignmentLeftStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class AlignmentLeftToolItem(style: AlignmentLeftStyle, itemClickAction: IToolbarItemClickAction? = null) :
    AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_align_left

    override fun iconClickHandle() {
        super.iconClickHandle()
        iconView.setIconResId(if (style.isChecked) R.mipmap.icon_toolitem_align_left_sel else R.mipmap.icon_toolitem_align_left)
    }
}