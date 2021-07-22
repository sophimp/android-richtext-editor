package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.LineSpaceSpan
import com.sophimp.are.style.LineSpaceStyle

class LineSpaceToolItem(style: LineSpaceStyle, private val isLarge: Boolean) :
    AbstractItem<LineSpaceSpan>(style) {
    override val srcResId: Int
        get() {
            return if (isLarge) R.mipmap.icon_toolitem_linespacing_enlarge
            else R.mipmap.icon_toolitem_linespacing_shorten
        }

}