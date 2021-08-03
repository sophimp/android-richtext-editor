package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.VideoSpan

class VideoStyle(editText: RichEditText) : BaseFreeStyle<VideoSpan>(editText) {


    fun addVideoSpan(path: String) {

//        val playIcon = BitmapFactory.decodeResource(res, R.mipmap.icon_video_play)
//        val videoCompose = mergeBitMapWithLimit(resource, playIcon, w, h)
//        val vd = BitmapDrawable(res, videoCompose)
//        vd.bounds = Rect(0, 0, w, h)
//        drawable.setDrawable(vd)
//        if (observer != null) {
//            observer.update(null, drawable)
//        }
    }

    override fun targetClass(): Class<VideoSpan> {
        return VideoSpan::class.java
    }

}