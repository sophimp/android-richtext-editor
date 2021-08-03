package com.sophimp.are.activity

import com.sophimp.are.models.MediaInfo

/**
 *
 * @author: sfx
 * @since: 2021/8/3
 */
interface IMediaChooseListener {
    fun onMediaChoose(mediaInfos: List<MediaInfo>)
}