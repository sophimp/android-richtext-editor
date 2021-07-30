package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.QuoteStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class QuoteToolItem(style: QuoteStyle, itemClickAction: IToolbarItemClickAction? = null) :
    AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_quote

}