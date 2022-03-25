package com.sophimp.are

import android.content.Context
import android.text.Spanned
import com.sophimp.are.spans.*

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
    fun onClickImage(context: Context, editable: Spanned, imageSpan: ImageSpan2?): Boolean = false

    /**
     * Do your actions upon span clicking [TableSpan]
     *
     * @return handled return true; or else return false
     */
    fun onClickTable(context: Context, editable: Spanned, tableSpan: TableSpan?): Boolean = false

    /**
     * Do your actions upon span clicking [UrlSpan]
     *
     * @return handled return true; or else return false
     */
    fun onClickUrl(context: Context, editable: Spanned, urlSpan: UrlSpan?): Boolean = false

    /**
     * Do your actions upon span clicking [AudioSpan]
     *
     * @return handled return true; or else return false
     */
    fun onClickAudio(context: Context, editable: Spanned, audioSpan: AudioSpan?): Boolean = false

    /**
     * Do your actions upon span clicking [VideoSpan]
     *
     * @return handled return true; or else return false
     */
    fun onClickVideo(context: Context, editable: Spanned, videoSpan: VideoSpan?): Boolean = false

    /**
     * Do your actions upon span clicking [AttachmentSpan]
     *
     * @return handled return true; or else return false
     */
    fun onClickAttachment(context: Context, editable: Spanned, attachmentSpan: AttachmentSpan?): Boolean = false

}