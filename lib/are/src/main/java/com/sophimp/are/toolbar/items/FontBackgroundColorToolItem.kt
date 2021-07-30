package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.FontBackgroundStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class FontBackgroundColorToolItem(style: FontBackgroundStyle, itemClickAction: IToolbarItemClickAction? = null) :
    AbstractItem(style, itemClickAction) {

    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_background

}