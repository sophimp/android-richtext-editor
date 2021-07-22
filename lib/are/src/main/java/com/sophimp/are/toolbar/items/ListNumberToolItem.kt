package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.ListNumberSpan
import com.sophimp.are.style.ListNumberStyle

class ListNumberToolItem(style: ListNumberStyle) :
    AbstractItem<ListNumberSpan>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_listnumber

}