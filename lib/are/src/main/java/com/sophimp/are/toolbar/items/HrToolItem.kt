package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.HrStyle

class HrToolItem(style: HrStyle) :
    AbstractItem(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_hr_line

}