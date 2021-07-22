package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.SubscriptSpan2
import com.sophimp.are.style.SubscriptStyle

class SubscriptToolItem(style: SubscriptStyle) : AbstractItem<SubscriptSpan2>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_subscript

}