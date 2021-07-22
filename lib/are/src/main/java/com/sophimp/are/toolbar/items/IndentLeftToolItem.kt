package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.IndentLeftStyle

class IndentLeftToolItem(style: IndentLeftStyle) :
    AbstractItem(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_indent_left

}