package com.sophimp.are

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
    fun onClickImage(editText: RichEditText, imageSpan: ImageSpan2?): Boolean = false

    /**
     * Do your actions upon span clicking [TableSpan]
     *
     * @return handled return true; or else return false
     */
    fun onClickTable(editText: RichEditText, videoSpan: TableSpan?): Boolean = false

    /**
     * Do your actions upon span clicking [UrlSpan]
     *
     * @return handled return true; or else return false
     */
    fun onClickUrl(editText: RichEditText, urlSpan: UrlSpan?): Boolean = false

    /**
     * Do your actions upon span clicking [AudioSpan]
     *
     * @return handled return true; or else return false
     */
    fun onClickAudio(editText: RichEditText, audioSpan: AudioSpan?): Boolean = false

    /**
     * Do your actions upon span clicking [VideoSpan]
     *
     * @return handled return true; or else return false
     */
    fun onClickVideo(editText: RichEditText, videoSpan: VideoSpan?): Boolean = false

    /**
     * Do your actions upon span clicking [AttachmentSpan]
     *
     * @return handled return true; or else return false
     */
    fun onClickAttachment(editText: RichEditText, videoSpan: AttachmentSpan?): Boolean = false

}