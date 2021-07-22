package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.IndentSpan
import com.sophimp.are.style.IndentRightStyle

class IndentRightToolItem(style: IndentRightStyle) :
    AbstractItem<IndentSpan>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_indent_right

}