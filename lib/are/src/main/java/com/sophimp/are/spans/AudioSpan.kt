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
    private val mAudioSize: Int?,
    private val mAudioDuration: String
) : ImageSpan(mContext, bitmapDrawable!!), ISpan, IClickableSpan, IUploadSpan {
    private var mUploadTime: String? = null

    enum class AudioType {
        LOCAL, SERVER, UNKNOWN
    }

    override val html: String
        get() {
            val htmlBuffer = StringBuilder("<audio url=\"")
            if (TextUtils.isEmpty(audioUrl)) {
                htmlBuffer.append(audioPath)
            } else {
                htmlBuffer.append(audioUrl)
            }
            htmlBuffer.append(" name=\"")
            htmlBuffer.append(mAudioName)
            htmlBuffer.append("\" size=\"")
            htmlBuffer.append(mAudioSize)
            htmlBuffer.append("\" upload-time=\"")
            htmlBuffer.append(mUploadTime)
            htmlBuffer.append("\" duration=\"")
            htmlBuffer.append(mAudioDuration)
            htmlBuffer.append("\" />")
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


    override fun uploadPath(): String {
        return audioPath
    }

    override fun uploadFileSize(): Int? {
        return mAudioSize
    }

}