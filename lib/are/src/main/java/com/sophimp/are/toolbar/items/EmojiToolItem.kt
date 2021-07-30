package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.EmojiStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class EmojiToolItem(style: EmojiStyle, itemClickAction: IToolbarItemClickAction? = null) : AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_emoji

    override fun iconClickHandle() {
        super.iconClickHandle()
    }
}