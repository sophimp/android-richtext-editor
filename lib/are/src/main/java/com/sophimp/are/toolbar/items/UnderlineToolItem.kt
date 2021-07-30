package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.UnderlineStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class UnderlineToolItem(style: UnderlineStyle, itemClickAction: IToolbarItemClickAction? = null) : AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_underline_unchecked

    override fun iconClickHandle() {
        super.iconClickHandle()
        iconView.setImageResource(if (style.isChecked) R.mipmap.icon_toolitem_underline_checked else R.mipmap.icon_toolitem_underline_unchecked)
    }
}