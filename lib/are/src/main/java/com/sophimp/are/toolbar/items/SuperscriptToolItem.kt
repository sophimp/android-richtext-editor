package com.sophimp.are.toolbar.items

import android.graphics.Color
import com.sophimp.are.R
import com.sophimp.are.style.SuperscriptStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class SuperscriptToolItem(style: SuperscriptStyle, itemClickAction: IToolbarItemClickAction? = null) : AbstractItem(style, itemClickAction) {

    override fun iconClickHandle() {
        super.iconClickHandle()
        if (style.isChecked) {
            iconView.setBackgroundColor(Color.CYAN)
        } else {
            iconView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_superscript

}