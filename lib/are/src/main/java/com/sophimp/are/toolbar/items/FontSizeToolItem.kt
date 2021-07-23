package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.FontSizeStyle

class FontSizeToolItem(style: FontSizeStyle) :
    AbstractItem(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_fontsize

}