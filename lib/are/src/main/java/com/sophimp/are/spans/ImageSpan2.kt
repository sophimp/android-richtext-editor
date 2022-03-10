package com.sophimp.are.spans

import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.text.style.ImageSpan
import com.sophimp.are.AttachFileType
import com.sophimp.are.inner.Html
import java.io.File

/**
 * @author: sfx
 * @since: 2021/7/20
 */
class ImageSpan2(
    drawable: Drawable,
    var localPath: String?,
    var serverUrl: String?,
    var name: String? = "",
    var imageType: String,
    var size: Long = 0,
    var width: Int = drawable.intrinsicWidth,
    var height: Int = drawable.intrinsicHeight
) : ImageSpan(drawable), IClickableSpan, IUploadSpan, ISpan {

    var uploadTime: String? = null

    val fileSize: Int
        get() {
            if (size == 0L) {
                val file = File(localPath)
                if (file.exists()) {
                    size = file.length()
                }
            }
            return size.toInt()
        }

    override fun uploadPath(): String? {
        return localPath
    }

    override fun uploadFileSize(): Int {
        return fileSize
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val rect = drawable.bounds
        if (fm != null) {
            fm.ascent = -rect.bottom
            fm.descent = 0
            fm.top = fm.ascent
            fm.bottom = 0
        }
//        val paintFontMetrics = paint.fontMetrics
//        if (fm != null) {
//            fm.ascent = paintFontMetrics.ascent.toInt()
//            fm.bottom = paintFontMetrics.bottom.toInt()
//            fm.descent = paintFontMetrics.descent.toInt()
//            fm.leading = paintFontMetrics.leading.toInt()
//            fm.top = paintFontMetrics.top.toInt()
//        }
        return rect.right
    }

    override val html: String
        get() {
            var imageName = ""
            var index = -1
            val htmlBuffer = StringBuilder("<img src=\"")
            if (TextUtils.isEmpty(serverUrl)) {
                htmlBuffer.append(localPath)
                localPath?.let {
                    index = localPath!!.indexOf(".")
                    if (index >= 0) {
                        imageName = localPath!!.substring(index)
                    }
                }
            } else {
                htmlBuffer.append(Html.ossServer.getMemoAndDiaryImageUrl(serverUrl))
                serverUrl?.let {
                    index = serverUrl!!.indexOf(".")
                    if (index >= 0) {
                        imageName = serverUrl!!.substring(index)
                    }
                }
            }
            htmlBuffer.append("\" data-type=\"")
            htmlBuffer.append(imageType)
            htmlBuffer.append("\" data-url=\"")
            htmlBuffer.append(Html.ossServer.getMemoAndDiaryImageUrl(serverUrl))
            htmlBuffer.append("\" data-file-name=\"")
            htmlBuffer.append("")
            if (width > 0 && height > 0) {
                htmlBuffer.append("\" width=\"")
                if (AttachFileType.STICKER.attachmentValue == imageType) {
                    htmlBuffer.append(width)
                } else {
                    htmlBuffer.append((width / Html.sContext.resources.displayMetrics.density + 0.5f).toInt())
                }
                htmlBuffer.append("\" height=\"")
                if (AttachFileType.STICKER.attachmentValue == imageType) {
                    htmlBuffer.append(height)
                } else {
                    htmlBuffer.append((height / Html.sContext.resources.displayMetrics.density + 0.5f).toInt())
                }
            }
            htmlBuffer.append("\" data-file-size=\"")
            htmlBuffer.append(size)
            htmlBuffer.append("\" data-uploadtime=\"")
            htmlBuffer.append(uploadTime)
            htmlBuffer.append("\" />")
            return htmlBuffer.toString()
        }
}