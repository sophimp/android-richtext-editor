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
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.sophimp.are.Constants
import com.sophimp.are.R
import com.sophimp.are.Util
import com.sophimp.are.Util.mergeBitMapWithLimit
import com.sophimp.are.Util.view2Bitmap
import com.sophimp.are.inner.Html
import com.sophimp.are.render.GlideResTarget
import com.sophimp.are.spans.AttachmentSpan
import com.sophimp.are.spans.AudioSpan
import com.sophimp.are.spans.VideoSpan

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
            defaultDrawable?.setBounds(0, 0, defaultDrawable.intrinsicWidth, defaultDrawable.intrinsicHeight)
            val defaultSpan = VideoSpan(defaultDrawable!!, localPath, url, "", 0, 0)
            editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
            editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val resTarget = object : GlideResTarget(context,
                width,
                height,
                if (TextUtils.isEmpty(defaultSpan.localPath)) previewUrl else defaultSpan.localPath
            ) {
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

            Glide.with(Html.sContext).asBitmap().load(if (TextUtils.isEmpty(previewUrl)) defaultSpan.localPath else previewUrl).encodeQuality(10)
                .into(resTarget)
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
            defaultDrawable?.setBounds(0, 0, defaultDrawable.intrinsicWidth, defaultDrawable.intrinsicHeight)

            val view = LayoutInflater.from(Html.sContext).inflate(R.layout.layout_view_rich_media_preview, null)
            view.findViewById<ImageView>(R.id.edit_annex_icon_iv).setImageResource(R.mipmap.icon_video_play)
            view.findViewById<TextView>(R.id.edit_annex_title_tv).text = name
            view.findViewById<TextView>(R.id.edit_annex_subtitle_tv).setText(Util.getTimeDurationDesc(duration.toLong()))
                .toString() + "  " + Util.getFileSizeDesc(size.toLong())
            val bitmap = view2Bitmap(view)
            bitmap?.let {
                val defaultSpan = VideoSpan(BitmapDrawable(Html.sContext.resources, bitmap), localPath, url, name, size.toInt(), duration.toInt())
                editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
                editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
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
            defaultDrawable?.setBounds(0, 0, defaultDrawable.intrinsicWidth, defaultDrawable.intrinsicHeight)

            val view = LayoutInflater.from(Html.sContext).inflate(R.layout.layout_view_rich_media_preview, null)
            view.findViewById<ImageView>(R.id.edit_annex_icon_iv).setImageResource(R.mipmap.icon_video_play)
            view.findViewById<TextView>(R.id.edit_annex_title_tv).text = name
            view.findViewById<TextView>(R.id.edit_annex_subtitle_tv).setText(Util.getTimeDurationDesc(duration.toLong()))
                .toString() + "  " + Util.getFileSizeDesc(size.toLong())
            val bitmap = view2Bitmap(view)
            bitmap?.let {
                val defaultSpan = AudioSpan(BitmapDrawable(Html.sContext.resources, bitmap), localPath, url, name, size.toInt(), duration)
                editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
                editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
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
            attachType: String
        ) {
            defaultDrawable?.setBounds(0, 0, defaultDrawable.intrinsicWidth, defaultDrawable.intrinsicHeight)

            val view = LayoutInflater.from(Html.sContext).inflate(R.layout.layout_view_rich_media_preview, null)
            view.findViewById<ImageView>(R.id.edit_annex_icon_iv).setImageResource(R.mipmap.icon_video_play)
            view.findViewById<TextView>(R.id.edit_annex_title_tv).text = name
            view.findViewById<TextView>(R.id.edit_annex_subtitle_tv).text = ""
            val bitmap = view2Bitmap(view)
            bitmap?.let {
                val defaultSpan = AttachmentSpan(BitmapDrawable(Html.sContext.resources, bitmap), localPath, url, name, size.toInt(), attachType)
                editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR)
                editable.setSpan(defaultSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

}