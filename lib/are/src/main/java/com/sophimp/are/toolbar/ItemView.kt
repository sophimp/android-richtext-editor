package com.sophimp.are.toolbar

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import com.sophimp.are.databinding.ToolbarItemViewBinding

/**
 *
 * @author: sfx
 * @since: 2021/7/30
 */
class ItemView(context: Context) : FrameLayout(context) {
    var binding: ToolbarItemViewBinding

    init {
        binding = ToolbarItemViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setIconResId(resId: Int) {
        binding.ivIcon.setImageResource(resId)
    }

    fun setIconImage(icon: Drawable) {
        binding.ivIcon.setImageDrawable(icon)
    }

    fun setIconBackground(bg: Drawable) {
        binding.ivIcon.background = bg
    }

    fun setMarkVisible(visible: Int) {
        binding.tvIconMark.visibility = visible
    }

    fun setMarkText(text: String) {
        binding.tvIconMark.text = text
    }

    fun setMarkBackgroundColor(@ColorInt color: Int) {
        val bg = GradientDrawable()
        bg.cornerRadius = context.resources.displayMetrics.density * 12
        bg.colors = intArrayOf(color, color)
        val size = (context.resources.displayMetrics.density * 12).toInt()
        bg.setBounds(0, 0, size, size)
        binding.tvIconMark.background = bg
//        binding.tvIconMark.setBackgroundColor(color)
    }
}