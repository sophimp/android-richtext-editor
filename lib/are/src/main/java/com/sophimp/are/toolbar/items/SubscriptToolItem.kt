package com.sophimp.are.toolbar.items

import android.graphics.Color
import com.sophimp.are.R
import com.sophimp.are.style.SubscriptStyle

class SubscriptToolItem(style: SubscriptStyle) : AbstractItem(style) {

    override fun iconClickHandle() {
        super.iconClickHandle()
        if (style.isChecked) {
            iconView.setBackgroundColor(Color.CYAN)
        } else {
            iconView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_subscript

}