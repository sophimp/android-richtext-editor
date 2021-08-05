package com.sophimp.are.spans

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.text.style.ImageSpan
import java.io.File

/**
 * @author: sfx
 * @since: 2021/7/20
 */
class ImageSpan2(
    drawable: Drawable,
    var localPath: String?,
    var url: String?,
    var name: String? = "",
    var size: Long? = 0,
    var width: Int = drawable.intrinsicWidth,
    var height: Int = drawable.intrinsicHeight
) : ImageSpan(drawable), IClickableSpan, IUploadSpan, ISpan {
    enum class ImageType {
        URI, URL, RES
    }

    var uploadTime: String? = null
    var imageType: ImageType = ImageType.URI

    val fileSize: Int
        get() {
            if (size!!.toLong() == 0L) {
                val file = File(localPath)
                if (file.exists()) {
                    size = file.length()
                }
            }
            return size!!.toInt()
        }

    override fun uploadPath(): String? {
        return localPath
    }

    override fun uploadFileSize(): Int? {
        return fileSize
    }

    override val html: String
        get() {
            val htmlBuffer = StringBuilder("<img src=\"")
            if (TextUtils.isEmpty(url)) {
                htmlBuffer.append(localPath)
            } else {
                htmlBuffer.append(url)
            }
            htmlBuffer.append("\" name=\"")
            htmlBuffer.append(name)
            htmlBuffer.append("\" width=\"")
            htmlBuffer.append(width)
            htmlBuffer.append("\" height=\"")
            htmlBuffer.append(height)
            htmlBuffer.append("\" />")
            return htmlBuffer.toString()
        }
}