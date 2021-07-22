package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.SuperscriptSpan2
import com.sophimp.are.style.SuperscriptStyle

class SuperscriptToolItem(style: SuperscriptStyle) : AbstractItem<SuperscriptSpan2>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_superscript

}