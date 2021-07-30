package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.LineSpaceEnlargeStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class LineSpaceEnlargeToolItem(style: LineSpaceEnlargeStyle, itemClickAction: IToolbarItemClickAction? = null) :
    AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() {
            return R.mipmap.icon_toolitem_linespacing_enlarge
        }

}