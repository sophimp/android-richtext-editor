package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.ItalicStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class ItalicToolItem(style: ItalicStyle, itemClickAction: IToolbarItemClickAction? = null) :
    AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_italic_unchecked

    override fun iconClickHandle() {
        super.iconClickHandle()
        iconView.setIconResId(if (style.isChecked) R.mipmap.icon_toolitem_italic_checked else R.mipmap.icon_toolitem_italic_unchecked)
    }
}