package com.sophimp.are.spans

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.style.ImageSpan
import com.sophimp.are.Constants
import com.sophimp.are.R
import java.io.File

/**
 * @author: sfx
 * @since: 2021/7/20
 */
class ImageSpan2 : ImageSpan, IClickableSpan, IUploadSpan, ISpan {
    enum class ImageType {
        URI, URL, RES
    }

    private var mContext: Context
    var uri: Uri? = null
        private set
    var url: String? = null
        private set

    // 本地路径
    var localPath = ""
    var resId = 0
        private set
    var mImgName: String? = null
    var mImgSize: String? = null
    var mUploadTime: String? = null
    var width = 0
    var height = 0

    constructor(
        context: Context,
        bitmapDrawable: Bitmap?,
        uri: Uri?
    ) : super(context, bitmapDrawable!!) {
        mContext = context
        this.uri = uri
    }

    constructor(
        context: Context,
        url: String?
    ) : super(context.resources.getDrawable(R.mipmap.default_image)!!) {
        mContext = context
        this.url = url
    }

    constructor(
        context: Context,
        bitmapDrawable: Bitmap?,
        url: String
    ) : super(context, bitmapDrawable!!) {
        mContext = context
        this.url = url
        localPath = url
    }

    constructor(context: Context, drawable: Drawable?, url: String) : super(
        drawable!!,
        url
    ) {
        mContext = context
        this.url = url
        localPath = url
    }

    constructor(context: Context, resId: Int) : super(context, resId) {
        mContext = context
        this.resId = resId
    }

    constructor(context: Context, uri: Uri?) : super(context, uri!!) {
        mContext = context
        this.uri = uri
    }

    val fileSize: Long
        get() {
            if (mImgSize!!.toLong() == 0L) {
                val file = File(url)
                if (file.exists()) {
                    mImgSize = file.length().toString()
                }
            }
            return mImgSize!!.toLong()
        }

    override fun getSource(): String? {
        return if (uri != null) {
            uri.toString()
        } else if (url != null) {
            url
        } else {
            Constants.EMOJI + "|" + resId
        }
    }

    val imageType: ImageType
        get() = if (uri != null) {
            ImageType.URI
        } else if (url != null) {
            ImageType.URL
        } else {
            ImageType.RES
        }

    fun setUrl(url: String?) {
        this.url = url
    }

    override fun uploadPath(): String {
        return localPath
    }

    override fun uploadFileSize(): String {
        return fileSize.toString()
    }

    override val html: String?
        get() {
//            val density = mContext.resources.displayMetrics.density
            val htmlBuffer = StringBuilder("<img src=\"")
            htmlBuffer.append(url)
            htmlBuffer.append("\" data-type=\"")
            htmlBuffer.append(imageType)
            htmlBuffer.append("\" data-url=\"")
            htmlBuffer.append(url)
            htmlBuffer.append("\" data-file-name=\"")
            htmlBuffer.append(mImgName)
            htmlBuffer.append("\" width=\"")
            htmlBuffer.append(width)
            htmlBuffer.append("\" height=\"")
            htmlBuffer.append(height)
            htmlBuffer.append("\" data-file-size=\"")
            htmlBuffer.append(mImgSize)
            htmlBuffer.append("\" data-uploadtime=\"")
            htmlBuffer.append(mUploadTime)
            htmlBuffer.append("\" />")
            return htmlBuffer.toString()
        }
}