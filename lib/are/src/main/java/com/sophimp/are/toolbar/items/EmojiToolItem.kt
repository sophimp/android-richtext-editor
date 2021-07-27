package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.EmojiStyle

class EmojiToolItem(style: EmojiStyle) : AbstractItem(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_emoji

    override fun iconClickHandle() {
        super.iconClickHandle()
    }
}