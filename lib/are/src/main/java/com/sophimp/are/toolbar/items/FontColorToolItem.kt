package com.sophimp.are.toolbar.items

import android.view.Gravity
import com.sophimp.are.R
import com.sophimp.are.colorpicker.ColorPickerListener
import com.sophimp.are.colorpicker.ColorPickerWindow
import com.sophimp.are.style.FontColorStyle

class FontColorToolItem(style: FontColorStyle) :
    AbstractItem(style) {
    private val colorWindow = ColorPickerWindow(context)

    init {
        colorWindow.colorPickerListener = object : ColorPickerListener {
            override fun onPickColor(color: Int) {
                iconView.setBackgroundColor(color)
                style.onPickColor(color)
                colorWindow.dismiss()
            }
        }
    }

    override fun iconClickHandle() {
        super.iconClickHandle()
        colorWindow.showAsDropDown(iconView, -(iconView.x).toInt(), -(context.resources.displayMetrics.density * 80).toInt(), Gravity.TOP)
    }

    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_foregroundcolor

}