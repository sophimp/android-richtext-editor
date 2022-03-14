package com.sophimp.are.style

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
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
import java.io.File
import kotlin.math.max
import kotlin.math.min

class ImageStyle(editText: RichEditText) : BaseFreeStyle<ImageSpan2>(editText) {

    init {
        glideRequest = Glide.with(editText.context)
        val displayMetrics = editText.context.resources.displayMetrics
        sWidth = (displayMetrics.widthPixels - displayMetrics.density * 32).toInt()
        sHeight = displayMetrics.heightPixels
    }

    companion object {
        val defaultDrawable = ContextCompat.getDrawable(Html.sContext, R.mipmap.default_image)
        private var glideRequest: RequestManager? = null
        var imageLoadedListener: ImageLoadedListener? = null
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
            val ssb = SpannableStringBuilder()
            ssb.append(Constants.CHAR_NEW_LINE)
            ssb.append(Constants.ZERO_WIDTH_SPACE_STR)
            //多插个空格
            //多插个空格
            ssb.append(Constants.CHAR_NEW_LINE)
            ssb.append(" ")
            ssb.setSpan(imageSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            editable.replace(start, end, ssb)
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
            localPath: String
        ) {
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
                AttachFileType.IMG.attachmentValue,
                0,
                width,
                height
            )
            editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
            editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val resTarget = object : GlideResTarget(
                context,
                defaultSpan.width,
                defaultSpan.height,
                if (TextUtils.isEmpty(defaultSpan.localPath)) defaultSpan.serverUrl else defaultSpan.localPath
            ) {
                override fun handleLoadedBitmap(
                    compressBitmap: Bitmap,
                    w: Int,
                    h: Int,
                    path: String?
                ) {
                    path?.let {
                        replaceImageSpanAfterLoaded(
                            editable,
                            defaultSpan,
                            path,
                            compressBitmap,
                            w,
                            h
                        )
                    }
                }
            }

            if (!TextUtils.isEmpty(defaultSpan.localPath)) {
                // local path
                glideRequest?.asBitmap()?.load(File(defaultSpan.localPath))?.encodeQuality(10)
                    ?.into(resTarget)
            } else {
                // remote path
                glideRequest?.asBitmap()?.load(defaultSpan.serverUrl)?.encodeQuality(10)
                    ?.into(resTarget)
            }
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
            h: Int
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
                        defaultSpan.width,
                        defaultSpan.height
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
                    Html.imageLoadedListener?.onImageLoaded(editable, spanStart, spanEnd)
                }
            }
        }
    }

    /**
     * insert ImageSpan
     * if image width > screen width, will scale to screen width by ratio of origin "width : height"
     */
    fun addImageSpan(localPath: String, url: String) {
        val start = max(min(mEditText.selectionStart, mEditText.length()), 0)
//        mEditText.editableText.replace(start, mEditText.selectionEnd, "\uFFFc\n")
        addImageSpanToEditable(
            context,
            mEditText.editableText,
            mEditText.selectionEnd,
            defaultDrawable!!.intrinsicWidth,
            defaultDrawable.intrinsicHeight,
            url,
            localPath
        )
        mEditText.editableText.insert(mEditText.selectionEnd, Constants.CHAR_NEW_LINE)
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
        height: Int
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

        val resTarget = object : GlideResTarget(
            context,
            defaultSpan.width,
            defaultSpan.height,
            if (TextUtils.isEmpty(defaultSpan.localPath)) defaultSpan.serverUrl else defaultSpan.localPath
        ) {
            override fun handleLoadedBitmap(
                compressBitmap: Bitmap,
                w: Int,
                h: Int,
                path: String?
            ) {
                path?.let {
                    replaceImageSpanAfterLoaded(editable, defaultSpan, path, compressBitmap, w, h)
                }
            }
        }

        if (!TextUtils.isEmpty(defaultSpan.localPath)) {
            // local path
            glideRequest?.asBitmap()?.load(File(defaultSpan.localPath))?.encodeQuality(10)
                ?.into(resTarget)
        } else {
            // remote path
            glideRequest?.asBitmap()?.load(defaultSpan.serverUrl)?.encodeQuality(10)
                ?.into(resTarget)
        }
    }

}