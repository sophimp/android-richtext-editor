package com.sophimp.are.listener

import android.text.Spanned

/**
 * 图片加载成功Listener
 * create by sfx on 2022/3/8 16:50
 */
interface ImageLoadedListener {

    fun onImageLoaded(spanned: Spanned, start: Int, end: Int)

    fun onImageRefresh(start: Int, end: Int)
}