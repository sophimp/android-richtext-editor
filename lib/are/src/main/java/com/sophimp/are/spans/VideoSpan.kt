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
    var localPath: String?,
    var videoUrl: String?,
    var videoName: String? = "",
    var videoSize: Int? = 0,
    var videoDuration: Int? = 0
) : ImageSpan(
    drawable), ISpan, IClickableSpan, IUploadSpan {
    var uploadTime: String? = ""

    enum class VideoType {
        LOCAL, SERVER, UNKNOWN
    }

    override val html: String
        get() {
            val htmlBuffer = StringBuilder("<video url=\"")
            if (TextUtils.isEmpty(videoUrl)) {
                htmlBuffer.append(localPath)
            } else {
                htmlBuffer.append(videoUrl)
            }
            htmlBuffer.append("\" name=\"")
            htmlBuffer.append(videoName)
            htmlBuffer.append("\" />")
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

    override fun uploadPath(): String? {
        return localPath
    }

    override fun uploadFileSize(): Int? {
        return videoSize
    }
}