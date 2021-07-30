package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.IndentRightStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class IndentRightToolItem(style: IndentRightStyle, itemClickAction: IToolbarItemClickAction? = null) :
    AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_indent_right

}