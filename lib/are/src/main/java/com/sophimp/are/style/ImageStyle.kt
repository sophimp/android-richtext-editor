package com.sophimp.are.style

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.text.Editable
import android.text.Spannable
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.sophimp.are.R
import com.sophimp.are.RichEditText
import com.sophimp.are.render.GlideResTarget
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.ImageSpan2
import java.io.File
import kotlin.math.max
import kotlin.math.min

class ImageStyle(editText: RichEditText) : BaseFreeStyle<ImageSpan2>(editText) {
    val defaultDrawable = context.resources.getDrawable(R.mipmap.default_image)

    init {
        glideRequest = Glide.with(editText.context)
        val displayMetrics = editText.context.resources.displayMetrics
        sWidth = (displayMetrics.widthPixels - displayMetrics.density * 32).toInt()
        sHeight = displayMetrics.heightPixels
        defaultDrawable.setBounds(0, 0, defaultDrawable.intrinsicWidth, defaultDrawable.intrinsicHeight)
    }

    companion object {
        private lateinit var glideRequest: RequestManager
        private var sWidth = 0
        private var sHeight = 0


        /**
         * loadImage for html parse
         */
        fun addImageSpanToEditable(context: Context, editable: Editable, start: Int, defaultSpan: ImageSpan2) {
            editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val resTarget = object : GlideResTarget(context,
                defaultSpan.width,
                defaultSpan.height,
                if (TextUtils.isEmpty(defaultSpan.localPath)) defaultSpan.url else defaultSpan.localPath) {
                override fun handleLoadedBitmap(compressBitmap: Bitmap, w: Int, h: Int, path: String?) {
                    val imageSpans = editable.getSpans(0, editable.length, ImageSpan2::class.java)
                    for (image in imageSpans) {
                        if (TextUtils.equals(image.localPath, path) || TextUtils.equals(image.url, path)) {
                            val spanStart = editable.getSpanStart(image)
                            val spanEnd = editable.getSpanEnd(image)
                            editable.removeSpan(image)
                            val loadedDrawable = BitmapDrawable(context.resources, compressBitmap)
                            loadedDrawable.setBounds(0, 0, w, h)
                            val loadedImageSpan = ImageSpan2(loadedDrawable, defaultSpan.localPath, defaultSpan.url)
                            loadedImageSpan.size = defaultSpan.size
                            loadedImageSpan.name = defaultSpan.name
                            loadedImageSpan.uploadTime = defaultSpan.uploadTime
                            editable.setSpan(loadedImageSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            }

            if (!TextUtils.isEmpty(defaultSpan.localPath)) {
                // local path
                glideRequest.asBitmap().load(File(defaultSpan.localPath)).encodeQuality(10).into(resTarget)
            } else {
                // remote path
                glideRequest.asBitmap().load(defaultSpan.url).encodeQuality(10).into(resTarget)
            }
        }
    }

    /**
     * insert ImageSpan
     * if image width > screen width, will scale to screen width ratio to origin "width : height"
     */
    fun addImageSpan(path: String) {
        val start = max(min(mEditText.selectionStart, mEditText.length()), 0)
        mEditText.editableText.replace(start, mEditText.selectionEnd, "\uFFFc")
        val file = File(path)
        val defaultImage = ImageSpan2(defaultDrawable, if (file.exists()) path else "", if (file.exists()) "" else path).apply {
            this.width = defaultDrawable.intrinsicWidth
            this.height = defaultDrawable.intrinsicHeight
        }
        addImageSpanToEditable(context, mEditText.editableText, start, defaultImage)
    }


    override fun setSpan(span: ISpan, start: Int, end: Int) {
        mEditText.editableText.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    override fun targetClass(): Class<ImageSpan2> {
        return ImageSpan2::class.java
    }

}