package com.sophimp.are.toolbar.items

import android.view.Gravity
import com.sophimp.are.R
import com.sophimp.are.colorpicker.ColorPickerListener
import com.sophimp.are.colorpicker.ColorPickerWindow
import com.sophimp.are.style.FontColorStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class FontColorToolItem(style: FontColorStyle, itemClickAction: IToolbarItemClickAction? = null) :
    AbstractItem(style, itemClickAction) {
    private var colorWindow: ColorPickerWindow? = null

    init {
        if (itemClickAction == null) {
            colorWindow = ColorPickerWindow(context)
            colorWindow?.colorPickerListener = object : ColorPickerListener {
                override fun onPickColor(color: Int) {
                    iconView.setBackgroundColor(color)
                    style.onPickColor(color)
                    colorWindow?.dismiss()
                }
            }
        }
    }

    override fun iconClickHandle() {
        super.iconClickHandle()
        if (itemClickAction == null) {
            colorWindow?.showAsDropDown(iconView,
                -(iconView.x).toInt(),
                -(context.resources.displayMetrics.density * 80 + iconView.height).toInt(),
                Gravity.TOP)
        }
    }

    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_foregroundcolor

}