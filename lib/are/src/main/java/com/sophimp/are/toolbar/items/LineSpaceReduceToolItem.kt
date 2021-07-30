package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.LineSpaceReduceStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class LineSpaceReduceToolItem(style: LineSpaceReduceStyle, itemClickAction: IToolbarItemClickAction? = null) :
    AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() {
            return R.mipmap.icon_toolitem_linespacing_shorten
        }

}