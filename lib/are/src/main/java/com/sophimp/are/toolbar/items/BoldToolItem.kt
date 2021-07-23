package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.BoldStyle

class BoldToolItem(style: BoldStyle) : AbstractItem(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_bold_unchecked

    override fun iconClickHandle() {
        super.iconClickHandle()
        iconView.setImageResource(if (style.isChecked) R.mipmap.icon_toolitem_bold_checked else R.mipmap.icon_toolitem_bold_unchecked)
    }
}