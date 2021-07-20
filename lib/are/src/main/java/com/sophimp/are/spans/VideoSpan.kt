package com.sophimp.are.spans

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.text.style.ImageSpan

/**
 * @author: sfx
 * @since: 2021/7/20
 */
class VideoSpan : ImageSpan, ISpan, IClickableSpan, IUploadSpan {
    private var mContext: Context
    var videoPath = ""
        private set
    var videoUrl: String
    private var mVideoName: String
    private var mVideoSize: String
    private var mVideoDuration: String
    private var mUploadTime: String? = null

    constructor(
        context: Context,
        video: BitmapDrawable?,
        videoPath: String,
        videoUrl: String,
        videoName: String,
        videoSize: String,
        videoDuration: String
    ) : super(video!!, videoPath) {
        mContext = context
        this.videoPath = videoPath
        this.videoUrl = videoUrl
        mVideoName = videoName
        mVideoSize = videoSize
        mVideoDuration = videoDuration
    }

    enum class VideoType {
        LOCAL, SERVER, UNKNOWN
    }

    constructor(
        context: Context,
        bitmapDrawable: Bitmap?,
        videoPath: String,
        videoUrl: String,
        videoName: String,
        videoSize: String,
        videoDuration: String
    ) : super(context, bitmapDrawable!!) {
        mContext = context
        this.videoPath = videoPath
        this.videoUrl = videoUrl
        mVideoName = videoName
        mVideoSize = videoSize
        mVideoDuration = videoDuration
    }

    constructor(
        context: Context,
        drawable: Drawable?,
        videoPath: String,
        videoUrl: String,
        videoName: String,
        videoSize: String,
        videoDuration: String
    ) : super(drawable!!, videoUrl) {
        mContext = context
        this.videoPath = videoPath
        this.videoUrl = videoUrl
        mVideoName = videoName
        mVideoSize = videoSize
        mVideoDuration = videoDuration
    }

    override val html: String
        get() {
            val htmlBuffer = StringBuilder("<attachment data-url=\"")
            htmlBuffer.append(videoUrl)
            htmlBuffer.append("\" data-type=\"01\"")
            htmlBuffer.append(" data-file-name=\"")
            htmlBuffer.append(mVideoName)
            htmlBuffer.append("\" data-file-size=\"")
            htmlBuffer.append(mVideoSize)
            htmlBuffer.append("\" data-uploadtime=\"")
            htmlBuffer.append(mUploadTime)
            htmlBuffer.append("\" data-duration=\"")
            htmlBuffer.append(mVideoDuration)
            htmlBuffer.append("\" ></attachment>")
            return htmlBuffer.toString()
        }

    val videoType: VideoType
        get() {
            if (!TextUtils.isEmpty(videoUrl)) {
                return VideoType.SERVER
            }
            return if (!TextUtils.isEmpty(videoPath)) {
                VideoType.LOCAL
            } else VideoType.UNKNOWN
        }

    fun setUploadTime(uploadTime: String) {
        mUploadTime = uploadTime
    }

    fun getmVideoName(): String {
        return mVideoName
    }

    fun getmVideoDuration(): String {
        return mVideoDuration
    }

    fun getmUploadTime(): String? {
        return mUploadTime
    }

    fun getmVideoSize(): String {
        return mVideoSize
    }

    override fun uploadPath(): String {
        return videoPath
    }

    override fun uploadFileSize(): String {
        return mVideoSize
    }
}