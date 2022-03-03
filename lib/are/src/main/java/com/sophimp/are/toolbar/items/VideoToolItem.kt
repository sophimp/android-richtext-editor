package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.style.ImageStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

/**
 * @author: sfx
 * @since: 2021/7/21
 */
class VideoToolItem(style: ImageStyle, itemClickAction: IToolbarItemClickAction? = null) : AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_video

    override fun iconClickHandle() {
        super.iconClickHandle()
    }
}