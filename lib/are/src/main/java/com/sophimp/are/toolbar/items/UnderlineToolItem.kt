package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.UnderlineStyle

class UnderlineToolItem(style: UnderlineStyle) : AbstractItem(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_underline_unchecked

}