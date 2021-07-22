package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.ItalicStyle

class ItalicToolItem(style: ItalicStyle) :
    AbstractItem(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_italic_unchecked

}