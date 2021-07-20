package com.sophimp.are.spans

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.text.style.ImageSpan
import java.util.*

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
class AttachmentSpan(
    private val mContext: Context,
    bitmapDrawable: Bitmap?,
    attachmentPath: String,
    attachmentUrl: String,
    attachmentName: String,
    attachmentSize: String,
    attachmentType: String
) : ImageSpan(mContext, bitmapDrawable!!), ISpan, IClickableSpan, IUploadSpan {
    val attachmentPath: String
    var attachmentUrl: String
    private val mAttachmentName: String
    private val mAttachmentSize: String
    private val mAttachmentType: String
    private var mUploadTime: String? = null
    val spanId: String

    override val html: String?
        get() {
            val htmlBuffer = StringBuilder("<attachment data-url=\"")
            if (TextUtils.isEmpty(attachmentUrl)) {
                htmlBuffer.append(attachmentPath)
            } else {
                htmlBuffer.append(attachmentUrl)
            }
            htmlBuffer.append("\" data-type=\"")
            htmlBuffer.append(mAttachmentType)
            htmlBuffer.append("\" data-file-name=\"")
            htmlBuffer.append(mAttachmentName)
            htmlBuffer.append("\" data-file-size=\"")
            htmlBuffer.append(mAttachmentSize)
            htmlBuffer.append("\" data-uploadtime=\"")
            htmlBuffer.append(mUploadTime)
            htmlBuffer.append("\" data-duration=\"")
            htmlBuffer.append("0")
            htmlBuffer.append("\" ></attachment>")
            return htmlBuffer.toString()
        }

    fun setUploadTime(uploadTime: String) {
        mUploadTime = uploadTime
    }

    fun getmAttachmentType(): String {
        return mAttachmentType
    }

    fun getmAttachmentName(): String {
        return mAttachmentName
    }

    fun getmAttachmentSize(): String {
        return mAttachmentSize
    }

    fun getmUploadTime(): String? {
        return mUploadTime
    }

    override fun uploadPath(): String {
        return attachmentPath
    }

    override fun uploadFileSize(): String {
        return mAttachmentSize
    }

    init {
        spanId = UUID.randomUUID().toString()
        this.attachmentPath = attachmentPath
        this.attachmentUrl = attachmentUrl
        mAttachmentName = attachmentName
        mAttachmentSize = attachmentSize
        mAttachmentType = attachmentType
    }
}