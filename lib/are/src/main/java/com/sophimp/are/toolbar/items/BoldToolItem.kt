package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.BoldSpan
import com.sophimp.are.style.BoldStyle

class BoldToolItem(style: BoldStyle) : AbstractItem<BoldSpan>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_align_right

}