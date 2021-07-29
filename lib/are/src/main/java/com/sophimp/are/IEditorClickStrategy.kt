package com.sophimp.are

import android.content.Context
import com.sophimp.are.spans.ImageSpan2
import com.sophimp.are.spans.UrlSpan
import com.sophimp.are.spans.VideoSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/29
 */
interface IEditorClickStrategy {

    /**
     * Do your actions upon span clicking [ImageSpan2]
     *
     * @return handled return true; or else return false
     */
    fun onClickImage(context: Context?, imageSpan: ImageSpan2?): Boolean = false

    /**
     * Do your actions upon span clicking [UrlSpan]
     *
     * @return handled return true; or else return false
     */
    fun onClickUrl(context: Context?, urlSpan: UrlSpan?): Boolean = false

    /**
     * Do your actions upon span clicking [VideoSpan]
     *
     * @return handled return true; or else return false
     */
    fun onClickVideo(context: Context?, videoSpan: VideoSpan?): Boolean = false

}