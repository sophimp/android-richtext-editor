package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.VideoSpan
import com.sophimp.are.style.VideoStyle

/**
 * @author: sfx
 * @since: 2021/7/21
 */
class VideoToolItem(style: VideoStyle) : AbstractItem<VideoSpan>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_video

    override fun iconClickHandle() {
        super.iconClickHandle()
    }
}