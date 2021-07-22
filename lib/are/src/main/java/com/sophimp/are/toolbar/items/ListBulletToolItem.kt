package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.ListBulletSpan
import com.sophimp.are.style.ListBulletStyle

class ListBulletToolItem(style: ListBulletStyle) :
    AbstractItem<ListBulletSpan>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_listbullet

}