package com.sophimp.are.spans

import android.graphics.drawable.BitmapDrawable
import android.text.TextUtils
import android.text.style.ImageSpan
import com.sophimp.are.inner.Html

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
class AudioSpan(
    bitmapDrawable: BitmapDrawable,
    var localPath: String?,
    var serverUrl: String?,
    var mAudioName: String?,
    var mAudioSize: Int,
    var mAudioDuration: String
) : ImageSpan(bitmapDrawable), ISpan, IClickableSpan, IUploadSpan {
    private var mUploadTime: String? = null

    override val html: String
        get() {
            val htmlBuffer = StringBuilder("<attachment data-url=\"")
            if (TextUtils.isEmpty(serverUrl)) {
                htmlBuffer.append(Html.ossServer.getMemoAndDiaryImageUrl(localPath))
            } else {
                htmlBuffer.append(Html.ossServer.getMemoAndDiaryImageUrl(serverUrl))
            }
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

    override fun uploadPath(): String? {
        return localPath
    }

    override fun uploadFileSize(): Int {
        return mAudioSize
    }

}