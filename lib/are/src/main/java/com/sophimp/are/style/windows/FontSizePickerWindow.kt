package com.sophimp.are.style.windows

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.sophimp.are.R
import com.sophimp.are.Util.getPixelByDp
import com.sophimp.are.Util.getScreenWidthAndHeight

class FontSizePickerWindow(
    private val mContext: Context,
    private val mListener: FontSizeChangeListener?
) : PopupWindow() {
    private val mRootView: View
    private var mPreview: TextView? = null
    private var mSeekbar: SeekBar? = null
    private fun inflateContentView(): View {
        val layoutInflater = LayoutInflater.from(mContext)
        return layoutInflater.inflate(R.layout.are_fontsize_picker, null)
    }

    private fun <T : View?> findViewById(id: Int): T {
        return mRootView.findViewById<T>(id)
    }

    fun setFontSize(size: Int) {
        var size = size
        size -= FONT_SIZE_BASE
        mSeekbar!!.progress = size
    }

    private fun initView() {
        mPreview = findViewById<TextView>(R.id.are_fontsize_preview)
        mSeekbar = findViewById<SeekBar>(R.id.are_fontsize_seekbar)
    }

    private fun setupListeners() {
        mSeekbar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                changePreviewText(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun changePreviewText(progress: Int) {
        val size = FONT_SIZE_BASE + progress
        mPreview!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
        mPreview!!.text = size.toString() + "sp: Preview"
        mListener?.onFontSizeChange(size)
    }

    companion object {
        private const val FONT_SIZE_BASE = 12
    }

    init {
        mRootView = inflateContentView()
        this.contentView = mRootView
        val wh = getScreenWidthAndHeight(mContext)
        this.width = wh[0]
        val h = getPixelByDp(mContext, 100)
        this.height = h
        setBackgroundDrawable(BitmapDrawable())
        this.isOutsideTouchable = true
        this.isFocusable = true
        initView()
        setupListeners()
        //        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED
//                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
}