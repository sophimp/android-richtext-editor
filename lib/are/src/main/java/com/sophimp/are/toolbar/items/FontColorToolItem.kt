package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.FontColorStyle

class FontColorToolItem(style: FontColorStyle) :
    AbstractItem(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_foregroundcolor

}