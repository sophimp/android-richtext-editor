package com.sophimp.are.spans

import android.graphics.drawable.BitmapDrawable
import android.text.TextUtils
import android.text.style.ImageSpan
import com.sophimp.are.inner.Html
import java.util.*

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
class AttachmentSpan(
    bitmapDrawable: BitmapDrawable,
    var localPath: String?,
    var serverUrl: String?,
    var attachName: String?,
    var attachSize: Int,
    var attachValue: String,
    var uploadTime: String? = null,
    var spanId: String = UUID.randomUUID().toString()
) : ImageSpan(bitmapDrawable), ISpan, IClickableSpan, IUploadSpan {

    override val html: String
        get() {
            val htmlBuffer = StringBuilder("<attachment data-url=\"")
            if (TextUtils.isEmpty(serverUrl)) {
                htmlBuffer.append(Html.ossServer.getMemoAndDiaryImageUrl(localPath))
            } else {
                htmlBuffer.append(Html.ossServer.getMemoAndDiaryImageUrl(serverUrl))
            }
            htmlBuffer.append("\" data-type=\"")
            htmlBuffer.append(attachValue)
            htmlBuffer.append("\" data-file-name=\"")
            htmlBuffer.append(attachName)
            htmlBuffer.append("\" data-file-size=\"")
            htmlBuffer.append(attachSize)
            htmlBuffer.append("\" data-uploadtime=\"")
            htmlBuffer.append(uploadTime)
            htmlBuffer.append("\" data-duration=\"")
            htmlBuffer.append("0")
            htmlBuffer.append("\" ></attachment>")
            return htmlBuffer.toString()
        }

    override fun uploadPath(): String? {
        return localPath
    }

    override fun uploadFileSize(): Int {
        return attachSize
    }

}