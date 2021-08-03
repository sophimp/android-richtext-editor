package com.sophimp.are.spans

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.text.style.ImageSpan

/**
 * @author: sfx
 * @since: 2021/7/20
 */
class VideoSpan(
    drawable: Drawable,
    var localPath: String,
    var videoUrl: String,
    var videoName: String = "",
    var videoSize: String = "0",
    var videoDuration: String = ""
) : ImageSpan(
    drawable), ISpan, IClickableSpan, IUploadSpan {
    var uploadTime: String? = ""

    enum class VideoType {
        LOCAL, SERVER, UNKNOWN
    }

    override val html: String
        get() {
            val htmlBuffer = StringBuilder("<attachment data-url=\"")
            htmlBuffer.append(videoUrl)
            htmlBuffer.append("\" data-type=\"01\"")
            htmlBuffer.append(" data-file-name=\"")
            htmlBuffer.append(videoName)
            htmlBuffer.append("\" data-file-size=\"")
            htmlBuffer.append(videoSize)
            htmlBuffer.append("\" data-uploadtime=\"")
            htmlBuffer.append(uploadTime)
            htmlBuffer.append("\" data-duration=\"")
            htmlBuffer.append(videoDuration)
            htmlBuffer.append("\" ></attachment>")
            return htmlBuffer.toString()
        }

    val videoType: VideoType
        get() {
            if (!TextUtils.isEmpty(videoUrl)) {
                return VideoType.SERVER
            }
            return if (!TextUtils.isEmpty(localPath)) {
                VideoType.LOCAL
            } else VideoType.UNKNOWN
        }

    override fun uploadPath(): String {
        return localPath
    }

    override fun uploadFileSize(): String {
        return videoSize
    }
}