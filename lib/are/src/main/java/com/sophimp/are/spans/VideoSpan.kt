package com.sophimp.are.spans

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.text.style.ImageSpan
import com.sophimp.are.inner.Html

/**
 * @author: sfx
 * @since: 2021/7/20
 */
class VideoSpan(
    drawable: Drawable,
    var localPath: String?,
    var serverUrl: String?,
    var videoName: String? = "",
    var videoSize: Int = 0,
    var videoDuration: Int = 0
) : ImageSpan(
    drawable), ISpan, IClickableSpan, IUploadSpan {
    var uploadTime: String? = ""

    enum class VideoType {
        LOCAL, SERVER, UNKNOWN
    }

    override val html: String
        get() {
            val htmlBuffer = StringBuilder("<attachment data-url=\"")
            if (TextUtils.isEmpty(serverUrl)) {
                htmlBuffer.append(Html.ossServer.getMemoAndDiaryImageUrl(localPath))
            } else {
                htmlBuffer.append(Html.ossServer.getMemoAndDiaryImageUrl(serverUrl))
            }

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
            if (!TextUtils.isEmpty(serverUrl)) {
                return VideoType.SERVER
            }
            return if (!TextUtils.isEmpty(localPath)) {
                VideoType.LOCAL
            } else VideoType.UNKNOWN
        }

    override fun uploadPath(): String? {
        return localPath
    }

    override fun uploadFileSize(): Int {
        return videoSize
    }
}