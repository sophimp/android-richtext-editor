package com.sophimp.are.spans

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.text.style.ImageSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
class AudioSpan(
    private val mContext: Context,
    bitmapDrawable: Bitmap?,
    val audioPath: String,
    var audioUrl: String,
    private val mAudioName: String,
    private val mAudioSize: String,
    private val mAudioDuration: String
) : ImageSpan(mContext, bitmapDrawable!!), ISpan, IClickableSpan, IUploadSpan {
    private var mUploadTime: String? = null

    enum class AudioType {
        LOCAL, SERVER, UNKNOWN
    }

    override val html: String
        get() {
            val htmlBuffer = StringBuilder("<attachment data-url=\"")
            htmlBuffer.append(audioUrl)
            htmlBuffer.append("\" data-type=\"02\"")
            htmlBuffer.append(" data-file-name=\"")
            htmlBuffer.append(mAudioName)
            htmlBuffer.append("\" data-file-size=\"")
            htmlBuffer.append(mAudioSize)
            htmlBuffer.append("\" data-uploadtime=\"")
            htmlBuffer.append(mUploadTime)
            htmlBuffer.append("\" data-duration=\"")
            htmlBuffer.append(mAudioDuration)
            htmlBuffer.append("\" ></attachment>")
            return htmlBuffer.toString()
        }

    val videoType: AudioType
        get() {
            if (!TextUtils.isEmpty(audioUrl)) {
                return AudioType.SERVER
            }
            return if (!TextUtils.isEmpty(audioPath)) {
                AudioType.LOCAL
            } else AudioType.UNKNOWN
        }

    fun setUploadTime(uploadTime: String) {
        mUploadTime = uploadTime
    }

    fun getmAudioUrl(): String {
        return audioUrl
    }

    fun getmAudioName(): String {
        return mAudioName
    }

    fun getmAudioSize(): String {
        return mAudioSize
    }

    fun getmAudioDuration(): String {
        return mAudioDuration
    }

    fun getmUploadTime(): String? {
        return mUploadTime
    }

    override fun uploadPath(): String {
        return audioPath
    }

    override fun uploadFileSize(): String {
        return mAudioSize
    }

}