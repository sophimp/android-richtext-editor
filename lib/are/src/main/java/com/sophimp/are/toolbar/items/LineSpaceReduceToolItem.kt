package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.LineSpaceReduceStyle

class LineSpaceReduceToolItem(style: LineSpaceReduceStyle) :
    AbstractItem(style) {
    override val srcResId: Int
        get() {
            return R.mipmap.icon_toolitem_linespacing_shorten
        }

}