package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.FontSizeSpan
import com.sophimp.are.style.FontSizeStyle

class FontSizeToolItem(style: FontSizeStyle) :
    AbstractItem<FontSizeSpan>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_foregroundcolor

}