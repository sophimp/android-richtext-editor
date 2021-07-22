package com.sophimp.are.colorpicker

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
class ColorCheckedView(private val mContext: Context, private val mSize: Int) :
    View(mContext) {
    private fun initView() {
        var layoutParams = LinearLayout.LayoutParams(mSize, mSize)
        layoutParams.gravity = Gravity.CENTER
        setBackgroundColor(Color.WHITE)
    }

    init {
        initView()
    }
}