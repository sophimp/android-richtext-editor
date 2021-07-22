package com.sophimp.are.style.windows

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.sophimp.are.R
import com.sophimp.are.Util.getPixelByDp
import com.sophimp.are.Util.getScreenWidthAndHeight
import com.sophimp.are.colorpicker.ColorPickerListener
import com.sophimp.are.colorpicker.ColorPickerView

class ColorPickerWindow(
    private val mContext: Context,
    private val mColorPickerListener: ColorPickerListener
) : PopupWindow() {
    private val colorPickerView: ColorPickerView
    private fun inflateContentView(): ColorPickerView {
        val layoutInflater = LayoutInflater.from(mContext)
        return layoutInflater.inflate(R.layout.are_color_picker, null) as ColorPickerView
    }

    private fun <T : View?> findViewById(id: Int): T {
        return colorPickerView.findViewById<T>(id)
    }

    fun setColor(color: Int) {
        colorPickerView.setColor(color)
    }

    private fun setupListeners() {
        colorPickerView.setColorPickerListener(mColorPickerListener)
    }

    fun setBackgroundColor(backgroundColor: Int) {
        colorPickerView.setBackgroundColor(backgroundColor)
    }

    init {
        colorPickerView = inflateContentView()
        this.contentView = colorPickerView
        val wh = getScreenWidthAndHeight(mContext)
        this.width = wh[0]
        val h = getPixelByDp(mContext, 50)
        this.height = h
        setBackgroundDrawable(BitmapDrawable())
        this.isOutsideTouchable = true
        this.isFocusable = true
        setupListeners()
        //        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED
//                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
}