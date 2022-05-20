package com.sophimp.are.style

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.text.Editable
import android.text.Spannable
import android.text.TextUtils
import android.text.style.ImageSpan
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.sophimp.are.AttachFileType
import com.sophimp.are.Constants
import com.sophimp.are.R
import com.sophimp.are.RichEditText
import com.sophimp.are.inner.Html
import com.sophimp.are.listener.ImageLoadedListener
import com.sophimp.are.render.GlideResTarget
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.ImageSpan2
import com.sophimp.are.spans.VideoSpan
import com.sophimp.are.utils.Util
import java.io.File
import kotlin.math.max
import kotlin.math.min

class ImageStyle(editText: RichEditText) : BaseFreeStyle<ImageSpan2>(editText) {

    private var glideRequest: RequestManager = Glide.with(editText.context)

    init {
        val displayMetrics = editText.context.resources.displayMetrics
        sWidth = (displayMetrics.widthPixels - displayMetrics.density * 32).toInt()
        sHeight = displayMetrics.heightPixels
    }

    companion object {
        val defaultDrawable = ContextCompat.getDrawable(Html.sContext, R.mipmap.default_image)
        val cachePaths = mutableListOf<String>()
        var sWidth = 0
        var sHeight = 0

        /**
         * insert imageSpan
         */
        fun insertImageSpan(editText: RichEditText, imageSpan: ImageSpan) {
            val editable: Editable = editText.editableText
            val start: Int = editText.selectionStart
            val end: Int = editText.selectionEnd
            insertImageSpan(editable, imageSpan, start, end)
            editText.markChanged()
        }

        fun insertImageSpan(editable: Editable, imageSpan: ImageSpan, start: Int, end: Int) {
            uiHandler.post {
                editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
                editable.setSpan(imageSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        /**
         * loadImage for html parse
         */
        fun addImageSpanToEditable(
            context: Context,
            editable: Editable,
            start: Int,
            width: Int,
            height: Int,
            url: String,
            localPath: String,
            dataType: String?,
            uploadTime: String?
        ): ImageSpan2 {
            defaultDrawable?.intrinsicWidth?.let {
                defaultDrawable.setBounds(
                    0,
                    0,
                    it,
                    defaultDrawable.intrinsicHeight
                )
            }
            val defaultSpan = ImageSpan2(
                defaultDrawable!!,
                localPath,
                url,
                "",
                dataType ?: AttachFileType.IMG.attachmentValue,
                Util.getFileSize(localPath),
                width,
                height,
                uploadTime
            )
            editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
            editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return defaultSpan

        }


        /**
         * 替换已加载成功的图片
         */
        fun replaceImageSpanAfterLoaded(
            editable: Editable,
            defaultSpan: ImageSpan2,
            path: String,
            compressBitmap: Bitmap,
            w: Int,
            h: Int,
            imageLoadedListener: ImageLoadedListener?
        ) {
            val imageSpans = editable.getSpans(0, editable.length, ImageSpan2::class.java)
            imageSpans.forEach { imgSpan ->
                if (TextUtils.equals(imgSpan.localPath, path)
                    || TextUtils.equals(imgSpan.serverUrl, path)
                ) {
                    val spanStart = editable.getSpanStart(imgSpan)
                    val spanEnd = editable.getSpanEnd(imgSpan)
                    val loadedDrawable = BitmapDrawable(compressBitmap)
                    loadedDrawable.setBounds(0, 0, w, h)
                    val loadedImageSpan = ImageSpan2(
                        loadedDrawable,
                        defaultSpan.localPath,
                        defaultSpan.serverUrl,
                        defaultSpan.name,
                        defaultSpan.imageType,
                        defaultSpan.size,
                        w,
                        h,
                        defaultSpan.uploadTime
                    )
                    loadedImageSpan.size = defaultSpan.size
                    loadedImageSpan.name = defaultSpan.name
                    loadedImageSpan.uploadTime = defaultSpan.uploadTime
                    editable.setSpan(
                        loadedImageSpan,
                        spanStart,
                        spanEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    editable.removeSpan(imgSpan)
                    imageLoadedListener?.onImageRefresh(spanStart, spanEnd)
                }
            }
        }
    }

    /**
     * 依次添加多张图片
     */
    fun addLocalImages(localPaths: List<String>, finish: (() -> Unit)?) {
        cachePaths.clear()
        cachePaths.addAll(localPaths)
        if (cachePaths.isNotEmpty()) {
            addImageSpan(cachePaths.removeAt(0), finish)
        }
    }

    /**
     * insert local path ImageSpan
     * if image width > screen width, will scale to screen width by ratio of origin "width : height"
     */
    private fun addImageSpan(localPath: String, finish: (() -> Unit)?) {
        val start = max(min(mEditText.selectionStart, mEditText.length()), 0)
        uiHandler.post {
            val resTarget = object : GlideResTarget(
                context,
                defaultDrawable?.intrinsicWidth ?: 60,
                defaultDrawable?.intrinsicHeight ?: 60,
                localPath
            ) {
                override fun handleLoadedBitmap(
                    compressBitmap: Bitmap,
                    w: Int,
                    h: Int,
                    path: String?
                ) {
                    var width = compressBitmap.width
                    var height = compressBitmap.height
                    if (width > sWidth) {
                        height = (sWidth / width) * compressBitmap.height
                        width = sWidth
                    }
//                    LogUtils.d("sgx compressBitmap width: ${compressBitmap.width} height: ${compressBitmap.height}")
                    val drawable = BitmapDrawable(mEditText.resources, compressBitmap)
                    drawable.setBounds(0, 0, compressBitmap.width, compressBitmap.height)
                    val loadedImageSpan = ImageSpan2(
                        drawable,
                        localPath,
                        "",
                        "",
                        AttachFileType.IMG.attachmentValue,
                        0,
                        width,
                        height,
                        ""
                    )
                    val editable = mEditText.editableText
                    editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
                    editable.insert(start + 1, Constants.CHAR_NEW_LINE)
                    editable.setSpan(
                        loadedImageSpan,
                        start,
                        start + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (cachePaths.isNotEmpty()) {
                        uiHandler.postDelayed({
                            addImageSpan(cachePaths.removeAt(0), finish)
                        }, 60)
                    } else {
                        mEditText.refreshRange(0, mEditText.length())
                        finish?.invoke()
                    }
                }
            }
            // local path
            glideRequest.asBitmap().load(File(localPath)).encodeQuality(10).into(resTarget)
            mEditText.markChanged()
        }
    }


    override fun setSpan(span: ISpan, start: Int, end: Int) {
        mEditText.editableText.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    override fun targetClass(): Class<ImageSpan2> {
        return ImageSpan2::class.java
    }

    /**
     * 插入 sticker
     */
    fun addStickerImage(
        url: String,
        width: Int,
        height: Int,
        imageLoadedListener: ImageLoadedListener?
    ) {
//        AttachmentType.STICKER.getAttachmentValue(),
        defaultDrawable?.setBounds(
            0,
            0,
            width,
            height
        )
        val defaultSpan = ImageSpan2(
            defaultDrawable!!,
            "",
            url,
            "",
            AttachFileType.STICKER.attachmentValue,
            0,
            width,
            height
        )
        val editable = mEditText.editableText
        val start = mEditText.selectionEnd
        editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
        editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        loadImageSpanWithGlide(context, defaultSpan, imageLoadedListener)
    }

    /**
     * 加载图片
     */
    fun loadImageSpanWithGlide(
        context: Context,
        imgSpan: ImageSpan2,
        imageLoadedListener: ImageLoadedListener?
    ) {
        val resTarget = object : GlideResTarget(
            context,
            imgSpan.width,
            imgSpan.height,
            if (TextUtils.isEmpty(imgSpan.localPath)) imgSpan.serverUrl else imgSpan.localPath
        ) {
            override fun handleLoadedBitmap(
                compressBitmap: Bitmap,
                w: Int,
                h: Int,
                path: String?
            ) {
                path?.let {
                    replaceImageSpanAfterLoaded(
                        mEditText.editableText,
                        imgSpan,
                        path,
                        compressBitmap,
                        compressBitmap.width,
                        compressBitmap.height,
                        imageLoadedListener
                    )
                }
            }
        }

        if (!TextUtils.isEmpty(imgSpan.localPath)) {
            // local path
            glideRequest.asBitmap().load(File(imgSpan.localPath)).encodeQuality(10).into(resTarget)
        } else {
            // remote path
            glideRequest.asBitmap().load(imgSpan.serverUrl).encodeQuality(10).into(resTarget)
        }
    }

    /**
     * 加载带预览图的VideoSpan
     */
    fun loadVideoSpanPreviewFrame(
        context: Context,
        videoSpan: VideoSpan,
        imageLoadedListener: ImageLoadedListener?
    ) {
        val resTarget = object : GlideResTarget(
            context,
            videoSpan.previewWidth,
            videoSpan.previewHeight,
            videoSpan.previewUrl
        ) {
            override fun handleLoadedBitmap(
                compressBitmap: Bitmap,
                w: Int,
                h: Int,
                path: String?
            ) {
                val editable = mEditText.editableText
                val videoSpans = editable.getSpans(0, editable.length, VideoSpan::class.java)
                for (span in videoSpans) {
                    if (TextUtils.equals(
                            span.localPath,
                            path
                        ) || TextUtils.equals(span.serverUrl, path)
                    ) {
                        val spanStart = editable.getSpanStart(span)
                        val spanEnd = editable.getSpanEnd(span)
                        editable.removeSpan(span)

                        val playIcon = BitmapFactory.decodeResource(
                            context.resources,
                            R.mipmap.icon_video_play
                        )
                        val videoCompose = Util.mergeBitMapWithLimit(compressBitmap, playIcon, w, h)
                        val loadedDrawable = BitmapDrawable(context.resources, videoCompose)
                        loadedDrawable.bounds = Rect(0, 0, w, h)
                        val loadedVideoSpan = VideoSpan(
                            loadedDrawable,
                            videoSpan.localPath,
                            videoSpan.serverUrl,
                            videoSpan.videoName,
                            videoSpan.videoSize,
                            videoSpan.videoDuration
                        )
                        loadedVideoSpan.uploadTime = videoSpan.uploadTime
                        editable.setSpan(
                            loadedVideoSpan,
                            spanStart,
                            spanEnd,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        imageLoadedListener?.onImageRefresh(spanStart, spanEnd)
                    }
                }
            }
        }
        glideRequest.asBitmap().load(videoSpan.previewUrl).encodeQuality(10).into(resTarget)
    }
}