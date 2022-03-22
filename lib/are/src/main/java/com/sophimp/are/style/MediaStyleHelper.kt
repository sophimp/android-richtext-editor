package com.sophimp.are.style

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.text.Editable
import android.text.Spannable
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.sophimp.are.AttachFileType
import com.sophimp.are.Constants
import com.sophimp.are.R
import com.sophimp.are.RichEditText
import com.sophimp.are.inner.Html
import com.sophimp.are.render.GlideResTarget
import com.sophimp.are.spans.AttachmentSpan
import com.sophimp.are.spans.AudioSpan
import com.sophimp.are.spans.TableSpan
import com.sophimp.are.spans.VideoSpan
import com.sophimp.are.utils.Util
import com.sophimp.are.utils.Util.mergeBitMapWithLimit
import com.sophimp.are.utils.Util.view2Bitmap

/**
 * 视频，音频，附件辅助类
 */
class MediaStyleHelper {

    init {
//        val displayMetrics = Html.sContext.resources.displayMetrics
//        width = (displayMetrics.widthPixels - displayMetrics.density * 24).toInt()
//        height = width * 9 / 16
    }

    companion object {
        val defaultDrawable = ContextCompat.getDrawable(Html.sContext, R.mipmap.icon_video_play)

        /**
         * loadImage for html parse
         */
        fun addFashionVideoSpanToEditable(
            context: Context,
            editable: Editable,
            start: Int,
            width: Int,
            height: Int,
            url: String,
            localPath: String,
            previewUrl: String
        ) {
            defaultDrawable?.setBounds(
                0,
                0,
                defaultDrawable.intrinsicWidth,
                defaultDrawable.intrinsicHeight
            )
            val defaultSpan = VideoSpan(defaultDrawable!!, localPath, url, "", 0, 0)
            editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
            editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val resTarget = object : GlideResTarget(
                context,
                width,
                height,
                if (TextUtils.isEmpty(defaultSpan.localPath)) previewUrl else defaultSpan.localPath
            ) {
                override fun handleLoadedBitmap(
                    compressBitmap: Bitmap,
                    w: Int,
                    h: Int,
                    path: String?
                ) {
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
                            val videoCompose = mergeBitMapWithLimit(compressBitmap, playIcon, w, h)
                            val loadedDrawable = BitmapDrawable(context.resources, videoCompose)
                            loadedDrawable.bounds = Rect(0, 0, w, h)
                            val loadedVideoSpan = VideoSpan(
                                loadedDrawable,
                                defaultSpan.localPath,
                                defaultSpan.serverUrl,
                                defaultSpan.videoName,
                                defaultSpan.videoSize,
                                defaultSpan.videoDuration
                            )
                            loadedVideoSpan.uploadTime = defaultSpan.uploadTime
                            editable.setSpan(
                                loadedVideoSpan,
                                spanStart,
                                spanEnd,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            Html.imageLoadedListener?.onImageRefresh(spanStart, spanEnd)
                        }
                    }
                }
            }

            Glide.with(Html.sContext).asBitmap()
                .load(if (TextUtils.isEmpty(previewUrl)) defaultSpan.localPath else previewUrl)
                .encodeQuality(10)
                .into(resTarget)
        }

        fun addDetailVideoSpanToEditable(
            editText: RichEditText,
            localPath: String,
            name: String,
            size: String
        ) {
            addDetailVideoSpanToEditable(
                editText.context,
                editText.editableText,
                editText.selectionEnd,
                "",
                localPath,
                name,
                size,
                "0"
            )
        }

        /**
         * video detail info
         */
        fun addDetailVideoSpanToEditable(
            context: Context,
            editable: Editable,
            start: Int,
            url: String,
            localPath: String,
            name: String,
            size: String,
            duration: String
        ) {
            defaultDrawable?.setBounds(
                0,
                0,
                defaultDrawable.intrinsicWidth,
                defaultDrawable.intrinsicHeight
            )

            val bitmapDrawable = generateCommonMediaDrawable(name,
                Util.getTimeDurationDesc(duration.toLong()) + "  " + Util.getFileSizeDesc(size.toLong()),
                AttachFileType.VIDEO.resId)
            bitmapDrawable.let {
                val defaultSpan = VideoSpan(
                    bitmapDrawable,
                    localPath,
                    url,
                    name,
                    size.toInt(),
                    duration.toInt()
                )
                editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
                editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        fun addDetailAudioSpanToEditable(
            editText: RichEditText,
            localPath: String,
            name: String,
            size: String,
            duration: String
        ) {
            addDetailAudioSpanToEditable(
                editText.context,
                editText.editableText,
                editText.selectionEnd,
                "",
                localPath,
                name,
                size,
                duration
            )
        }

        /**
         * audio detail info
         */
        fun addDetailAudioSpanToEditable(
            context: Context,
            editable: Editable,
            start: Int,
            url: String,
            localPath: String,
            name: String,
            size: String,
            duration: String
        ) {
            defaultDrawable?.setBounds(
                0,
                0,
                defaultDrawable.intrinsicWidth,
                defaultDrawable.intrinsicHeight
            )

            val bitmapDrawable = generateCommonMediaDrawable(name,
                Util.getTimeDurationDesc(duration.toLong()) + "  " + Util.getFileSizeDesc(size.toLong()),
                R.mipmap.icon_file_audio)
            bitmapDrawable.let {
                val defaultSpan = AudioSpan(
                    bitmapDrawable,
                    localPath,
                    url,
                    name,
                    size.toInt(),
                    duration
                )
                editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
                editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        fun addDetailAttachmentSpanToEditable(
            editText: RichEditText,
            localPath: String,
            name: String,
            size: String,
            attachValue: String
        ) {
            addDetailAttachmentSpanToEditable(
                editText.context,
                editText.editableText,
                editText.selectionEnd,
                "",
                localPath,
                name,
                size,
                attachValue
            )
        }

        /**
         * attachment files detail info
         */
        fun addDetailAttachmentSpanToEditable(
            context: Context,
            editable: Editable,
            start: Int,
            url: String,
            localPath: String,
            name: String,
            size: String,
            attachValue: String
        ) {
            defaultDrawable?.setBounds(
                0,
                0,
                defaultDrawable.intrinsicWidth,
                defaultDrawable.intrinsicHeight
            )
            val bitmapDrawable = generateCommonMediaDrawable(name, "", AttachFileType.getAttachmentTypeByValue(attachValue).resId)
            bitmapDrawable.let {
                val defaultSpan = AttachmentSpan(
                    bitmapDrawable,
                    localPath,
                    url,
                    name,
                    size.toInt(),
                    attachValue
                )
                editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
                editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        private fun generateCommonMediaDrawable(title: String, subTitle: String, @DrawableRes imgRes: Int): BitmapDrawable {
            val view = LayoutInflater.from(Html.sContext).inflate(R.layout.layout_view_rich_media_preview, null)
            view.findViewById<ImageView>(R.id.edit_annex_icon_iv)
                .setImageResource(imgRes)
            view.findViewById<TextView>(R.id.edit_annex_title_tv).text = title
            view.findViewById<TextView>(R.id.edit_annex_subtitle_tv).text = subTitle
            val bitmap = view2Bitmap(view)
            val drawable = BitmapDrawable(Html.sContext.resources, bitmap)
            val width: Int = drawable.intrinsicWidth
            val height: Int = drawable.intrinsicHeight
            drawable.setBounds(0, 0, if (width > 0) width else 0, if (height > 0) height else 0)
            return drawable
        }

        @JvmStatic
        fun insertTableSpan(editable: Editable, html: String, start: Int, end: Int) {
            val insertDrawable = generateCommonMediaDrawable("表格", "点击查看", R.mipmap.icon_file_excel)
            val tableSpan = TableSpan(html, insertDrawable)
            editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
            editable.setSpan(tableSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

    }
}