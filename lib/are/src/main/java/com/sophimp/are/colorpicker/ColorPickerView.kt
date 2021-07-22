package com.sophimp.are.colorpicker

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View.OnClickListener
import android.widget.HorizontalScrollView
import android.widget.LinearLayout

/**
 * @author: sfx
 * @since: 2021/7/21
 */
class ColorPickerView constructor(
    private val mContext: Context,
    private val mAttributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(mContext, mAttributeSet, defStyleAttr) {
    private var mColorsContainer: LinearLayout? = null
    private var mColorPickerListener: ColorPickerListener? = null
    private val mAttributeBundle = Bundle()
    private val mColorViewWidth = 0
    private val mColorViewHeight = 0
    private val mColorViewMarginLeft = 0
    private val mColorViewMarginRight = 0
    private val mColorCheckedViewType = 0
    private val mColors: IntArray? = null
    private fun initView() {
        mColorsContainer = LinearLayout(mContext)
        val containerLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        mColorsContainer!!.layoutParams = containerLayoutParams
        for (color in mColors!!) {
            val colorView = ColorView(mContext, color, mAttributeBundle)
            mColorsContainer!!.addView(colorView)
            colorView.setOnClickListener(OnClickListener {
                val isCheckedNow = colorView.checked
                if (isCheckedNow) {
                    if (mColorPickerListener != null) {
                        mColorPickerListener!!.onPickColor(colorView.color)
                    }
                    return@OnClickListener
                }
                val childCount = mColorsContainer!!.childCount
                for (i in 0 until childCount) {
                    val childView = mColorsContainer!!.getChildAt(i)
                    if (childView is ColorView) {
                        val isThisColorChecked =
                            childView.checked
                        if (isThisColorChecked) {
                            childView.checked = false
                        }
                    }
                }
                colorView.checked = true
                if (mColorPickerListener != null) {
                    mColorPickerListener!!.onPickColor(colorView.color)
                }
            })
        }
        this.addView(mColorsContainer)
    }

    fun setColorPickerListener(listener: ColorPickerListener?) {
        mColorPickerListener = listener
    }

    fun setColor(selectedColor: Int) {
        val childCount = mColorsContainer!!.childCount
        for (i in 0 until childCount) {
            val childView = mColorsContainer!!.getChildAt(i)
            if (childView is ColorView) {
                val viewColor = childView.color
                Log.w("ARE", "view/selected color $viewColor, $selectedColor")
                if (viewColor == selectedColor) {
                    childView.performClick()
                    break
                }
            }
        }
    }

    init {

//        TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.ColorPickerView);
//        mColorViewWidth = ta.getDimensionPixelSize(R.styleable.ColorPickerView_colorViewWidth, 40);
//        mColorViewHeight = ta.getDimensionPixelSize(R.styleable.ColorPickerView_colorViewHeight, 40);
//        mColorViewMarginLeft = ta.getDimensionPixelSize(R.styleable.ColorPickerView_colorViewMarginLeft, 5);
//        mColorViewMarginRight = ta.getDimensionPixelSize(R.styleable.ColorPickerView_colorViewMarginRight, 5);
//        mColorCheckedViewType = ta.getInt(R.styleable.ColorPickerView_colorViewCheckedType, 0);
//        int colorsId = ta.getResourceId(R.styleable.ColorPickerView_colors, R.array.colors);
//        mColors = ta.getResources().getIntArray(colorsId);
//        ta.recycle();
        mAttributeBundle.putInt(ColorView.ATTR_VIEW_WIDTH, mColorViewWidth)
        mAttributeBundle.putInt(ColorView.ATTR_VIEW_HEIGHT, mColorViewWidth)
        mAttributeBundle.putInt(ColorView.ATTR_MARGIN_LEFT, mColorViewMarginLeft)
        mAttributeBundle.putInt(ColorView.ATTR_MARGIN_RIGHT, mColorViewMarginRight)
        mAttributeBundle.putInt(ColorView.ATTR_CHECKED_TYPE, mColorCheckedViewType)
        initView()
    }
}