package com.sophimp.are.colorpicker

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import com.sophimp.are.R

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
class ColorCheckedViewCheckmark(private val mContext: Context, private val mSize: Int) :
    AppCompatImageView(mContext) {
    private fun initView() {
        var layoutParams = LinearLayout.LayoutParams(mSize, mSize)
        layoutParams.gravity = Gravity.CENTER
        setImageResource(R.mipmap.icon_check_mark)
    }

    init {
        initView()
    }
}