package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.ListBulletStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class ListBulletToolItem(style: ListBulletStyle, itemClickAction: IToolbarItemClickAction? = null) :
    AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_list_bullet

}