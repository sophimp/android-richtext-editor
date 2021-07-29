package com.sophimp.are.style

import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.ImageSpan2

class ImageStyle(editText: RichEditText) : BaseFreeStyle<ImageSpan2>(editText) {

    companion object {
        const val REQUEST_CODE = 1001
        private lateinit var glideRequest: RequestManager
        private var sWidth = 0
        private var sHeight = 0
    }

    init {
        glideRequest = Glide.with(editText.context)
        val displayMetrics = editText.context.resources.displayMetrics
        sWidth = (displayMetrics.widthPixels - displayMetrics.density * 32).toInt()
        sHeight = displayMetrics.heightPixels
    }

    override fun setSpan(span: ISpan, start: Int, end: Int) {
        // generateSpan
//        val options = BitmapFactory.Options()
//        val bitmap: Bitmap = ImageUtils.getBitmapWithScreeWidth(
//            src as String?,
//            sWidth,
//            options
//        )
//            ?: return
//        var res = bitmap
//        if (bitmap.width > sWidth) {
//            val targetWidth: Int
//            val targetHeight: Int
//            targetWidth = sWidth
//            targetHeight =
//                (options.outHeight.toFloat() / options.outWidth.toFloat() * sWidth + 0.5f).toInt()
//            //            res = scale(bitmap, options.outWidth, options.outHeight);
////            float scale = (float) screenWidth / (float) oriWidth;
////            oriWidth = (int) (oriWidth * scale);
////            oriHeight = (int) (oriHeight * scale);
//            res = ImageUtils.scale(bitmap, targetWidth, targetHeight)
//            //            bitmap.recycle();
//        }
//        val imageSpan = DRImageSpan(mContext, res, src)
//        imageSpan.width = options.outWidth
//        imageSpan.height = options.outHeight
//        imageSpan.mImgSize = File(src).length().toString()
        val editable: Editable = mEditText.editableText
        val start: Int = mEditText.selectionStart
        val end: Int = mEditText.selectionEnd
        val ssb = SpannableStringBuilder()
        ssb.append(Constants.ZERO_WIDTH_SPACE_STR)
        ssb.append(Constants.ZERO_WIDTH_SPACE_STR)
        ssb.append("\n")
        ssb.setSpan(span, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        try {
            mEditText.stopMonitor()
            editable.replace(start, end, ssb)
            val text = " "
            editable.insert(end + 1, text)
            mEditText.startMonitor()
            mEditText.isChange = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun targetClass(): Class<ImageSpan2> {
        return ImageSpan2::class.java
    }

}