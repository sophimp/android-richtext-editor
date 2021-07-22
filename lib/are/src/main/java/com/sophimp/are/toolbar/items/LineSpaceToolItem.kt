package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.LineSpaceStyle

class LineSpaceToolItem(style: LineSpaceStyle) :
    AbstractItem(style) {
    override val srcResId: Int
        get() {
            return if ((style as LineSpaceStyle).isLarge) R.mipmap.icon_toolitem_linespacing_enlarge
            else R.mipmap.icon_toolitem_linespacing_shorten
        }

}