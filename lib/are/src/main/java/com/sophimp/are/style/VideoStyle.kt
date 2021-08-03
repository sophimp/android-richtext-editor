package com.sophimp.are.style

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.text.Editable
import android.text.Spannable
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.sophimp.are.R
import com.sophimp.are.RichEditText
import com.sophimp.are.Util.mergeBitMapWithLimit
import com.sophimp.are.render.GlideResTarget
import com.sophimp.are.spans.VideoSpan
import java.io.File
import kotlin.math.max
import kotlin.math.min

class VideoStyle(editText: RichEditText) : BaseFreeStyle<VideoSpan>(editText) {
    val defaultDrawable = context.resources.getDrawable(R.mipmap.icon_video_play)

    init {
        glideRequest = Glide.with(editText.context)
        val displayMetrics = editText.context.resources.displayMetrics
        width = (displayMetrics.widthPixels - displayMetrics.density * 24).toInt()
        height = width * 9 / 16
        defaultDrawable.setBounds(0, 0, defaultDrawable.intrinsicWidth, defaultDrawable.intrinsicHeight)
    }

    companion object {
        private lateinit var glideRequest: RequestManager
        private var width = 0
        private var height = 0


        /**
         * loadImage for html parse
         */
        fun addVideoSpanToEditable(context: Context, editable: Editable, start: Int, defaultSpan: VideoSpan) {
            editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val resTarget = object : GlideResTarget(context,
                width,
                height,
                if (TextUtils.isEmpty(defaultSpan.localPath)) defaultSpan.videoUrl else defaultSpan.localPath) {
                override fun handleLoadedBitmap(compressBitmap: Bitmap, w: Int, h: Int, path: String?) {
                    val videoSpans = editable.getSpans(0, editable.length, VideoSpan::class.java)
                    for (span in videoSpans) {
                        if (TextUtils.equals(span.localPath, path) || TextUtils.equals(span.videoUrl, path)) {
                            val spanStart = editable.getSpanStart(span)
                            val spanEnd = editable.getSpanEnd(span)
                            editable.removeSpan(span)

                            val playIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.icon_video_play)
                            val videoCompose = mergeBitMapWithLimit(compressBitmap, playIcon, w, h)
                            val loadedDrawable = BitmapDrawable(context.resources, videoCompose)
                            loadedDrawable.bounds = Rect(0, 0, w, h)
                            val loadedVideoSpan = VideoSpan(loadedDrawable,
                                defaultSpan.localPath,
                                defaultSpan.videoUrl,
                                defaultSpan.videoName,
                                defaultSpan.videoSize,
                                defaultSpan.videoDuration)
                            loadedVideoSpan.uploadTime = defaultSpan.uploadTime
                            editable.setSpan(loadedVideoSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            }

            if (!TextUtils.isEmpty(defaultSpan.localPath)) {
                // local path
                glideRequest.asBitmap().load(File(defaultSpan.localPath)).encodeQuality(10).into(resTarget)
            } else {
                // remote path
                glideRequest.asBitmap().load(defaultSpan.videoUrl).encodeQuality(10).into(resTarget)
            }
        }
    }

    /**
     * insert ImageSpan
     * if image width > screen width, will scale to screen width ratio to origin "width : height"
     */
    fun addVideoSpan(path: String) {
        val start = max(min(mEditText.selectionStart, mEditText.length()), 0)
        mEditText.editableText.replace(start, mEditText.selectionEnd, "\uFFFc\n")
        val file = File(path)
        val defaultImage = VideoSpan(defaultDrawable, if (file.exists()) path else "", if (file.exists()) "" else path)
        addVideoSpanToEditable(context, mEditText.editableText, start, defaultImage)
    }


    override fun targetClass(): Class<VideoSpan> {
        return VideoSpan::class.java
    }

}