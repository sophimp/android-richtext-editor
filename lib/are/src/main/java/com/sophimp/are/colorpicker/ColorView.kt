package com.sophimp.are.colorpicker

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout

/**
 * @author: sfx
 * @since: 2021/7/21
 */
class ColorView(private val mContext: Context, private var mColor: Int, attributeBundle: Bundle) :
    LinearLayout(mContext) {
    private var mColorViewWidth = 0
    private var mColorViewHeight = 0
    private var mColorViewMarginLeft = 0
    private var mColorViewMarginRight = 0
    private var mColorViewCheckedType = 0
    private var mChecked = false
    private var mCheckView: View? = null
    private fun initView() {
        mCheckView = checkView
        var layoutParams =
            LayoutParams(mColorViewWidth, mColorViewHeight)
        layoutParams.setMargins(mColorViewMarginLeft, 0, mColorViewMarginRight, 0)
        layoutParams = layoutParams
        setBackgroundColor(mColor)
        this.gravity = Gravity.CENTER
        this.addView(mCheckView)
        initCheckStatus()
    }

    private fun initCheckStatus() {
        if (mCheckView == null) {
            return
        }
        if (mChecked) {
            mCheckView!!.visibility = View.VISIBLE
        } else {
            mCheckView!!.visibility = View.GONE
        }
    }

    var color: Int
        get() = mColor
        set(color) {
            mColor = color
            initView()
        }

    var checkView: View?
        get() {
            if (mCheckView == null) {
                when (mColorViewCheckedType) {
                    CHECK_TYPE_DEFAULT -> mCheckView = ColorCheckedView(
                        mContext,
                        mColorViewWidth / DEFAULT_CHECK_VIEW_PERCENT
                    )
                    CHECK_TYPE_CHECK_MARK -> mCheckView =
                        ColorCheckedViewCheckmark(
                            mContext,
                            mColorViewWidth / CHECKMARK_CHECK_VIEW_PERCENT
                        )
                    else -> mCheckView = ColorCheckedView(
                        mContext,
                        mColorViewWidth / DEFAULT_CHECK_VIEW_PERCENT
                    )
                }
            }
            return mCheckView
        }
        set(checkedView) {
            mCheckView = checkedView
        }

    var checked: Boolean
        get() = mChecked
        set(checked) {
            mChecked = checked
            initCheckStatus()
        }

    companion object {
        const val ATTR_VIEW_WIDTH = "ATTR_VIEW_WIDTH"
        const val ATTR_VIEW_HEIGHT = "ATTR_VIEW_HEIGHT"
        const val ATTR_MARGIN_LEFT = "ATTR_MARGIN_LEFT"
        const val ATTR_MARGIN_RIGHT = "ATTR_MARGIN_RIGHT"
        const val ATTR_CHECKED_TYPE = "ATTR_CHECKED_TYPE"

        /**
         * If this view width = 80, the the default check view width = 10
         */
        private const val DEFAULT_CHECK_VIEW_PERCENT = 8
        private const val CHECKMARK_CHECK_VIEW_PERCENT = 2
        private const val CHECK_TYPE_DEFAULT = 0
        private const val CHECK_TYPE_CHECK_MARK = 1
    }

    init {
        mColorViewWidth = attributeBundle.getInt(ATTR_VIEW_WIDTH, 40)
        mColorViewHeight = attributeBundle.getInt(ATTR_VIEW_HEIGHT, 40)
        mColorViewMarginLeft = attributeBundle.getInt(ATTR_MARGIN_LEFT, 2)
        mColorViewMarginRight = attributeBundle.getInt(ATTR_MARGIN_RIGHT, 2)
        mColorViewCheckedType = attributeBundle.getInt(ATTR_CHECKED_TYPE, 0)
        initView()
    }
}