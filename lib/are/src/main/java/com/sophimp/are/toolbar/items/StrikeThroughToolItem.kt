package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.StrikethroughStyle

class StrikeThroughToolItem(style: StrikethroughStyle) :
    AbstractItem(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_strike_through_unchecked

    override fun iconClickHandle() {
        super.iconClickHandle()
        iconView.setImageResource(if (style.isChecked) R.mipmap.icon_toolitem_strike_through_checked else R.mipmap.icon_toolitem_strike_through_unchecked)
    }
}