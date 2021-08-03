package com.sophimp.are.render

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sophimp.are.Util.compressByScale

abstract class GlideResTarget(ctx: Context, width: Int, height: Int, var path: String?) : CustomTarget<Bitmap?>() {
    private val res: Resources = ctx.resources
    var w: Int
    var h: Int
    var hasReady = false
    private val maxWidth: Int

    init {
        w = width
        h = height
        maxWidth = (res.displayMetrics.widthPixels - res.displayMetrics.density * 12).toInt()
    }

    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
        hasReady = true
        val bw = resource.width
        val bh = resource.height
        var compressBitmap = resource
        if (w == 0 || bw > maxWidth) {
            val newWidth = maxWidth
            val newHeight = (bh.toFloat() / bw.toFloat() * newWidth + 0.5f).toInt()
            compressBitmap = compressByScale(resource, newWidth, newHeight, false)
            w = maxWidth
            h = (bh.toFloat() / bw.toFloat() * w + 0.5f).toInt()
        } else {
            w = bw
            h = bh
        }

        handleLoadedBitmap(compressBitmap, w, h, path)
//        val rect = Rect(0, 0, w, h)
//        drawable.w = w
//        drawable.h = h
//        drawable.defaultDrawable.bounds = rect
//        val sd = BitmapDrawable(res, compressBitmap ?: resource)
//        sd.bounds = rect
//        drawable.mDrawable = sd
//        if (observer != null) {
//            observer!!.update(null, drawable)
//        }
    }

    abstract fun handleLoadedBitmap(compressBitmap: Bitmap, w: Int, h: Int, path: String?)

    override fun onLoadCleared(placeholder: Drawable?) {}

}