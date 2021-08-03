package com.sophimp.are.spans

import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import java.io.File

/**
 * @author: sfx
 * @since: 2021/7/20
 */
class ImageSpan2(drawable: Drawable, var localPath: String?, var url: String?) : ImageSpan(drawable), IClickableSpan, IUploadSpan, ISpan {
    enum class ImageType {
        URI, URL, RES
    }

    var name: String? = null
    var size: String? = null
    var uploadTime: String? = null
    var imageType: ImageType = ImageType.URI
    var width = 0
    var height = 0

    val fileSize: Long
        get() {
            if (size!!.toLong() == 0L) {
                val file = File(localPath)
                if (file.exists()) {
                    size = file.length().toString()
                }
            }
            return size!!.toLong()
        }

    override fun uploadPath(): String? {
        return localPath
    }

    override fun uploadFileSize(): String {
        return fileSize.toString()
    }

    override val html: String
        get() {
            val htmlBuffer = StringBuilder("<img src=\"")
            htmlBuffer.append(url)
            htmlBuffer.append("\" name=\"")
            htmlBuffer.append(name)
            htmlBuffer.append("\" width=\"")
            htmlBuffer.append(width)
            htmlBuffer.append("\" height=\"")
            htmlBuffer.append(height)
            htmlBuffer.append("\"size=\"")
            htmlBuffer.append(size)
            htmlBuffer.append("\"uploadTime=\"")
            htmlBuffer.append(uploadTime)
            htmlBuffer.append("\" />")
            return htmlBuffer.toString()
        }
}