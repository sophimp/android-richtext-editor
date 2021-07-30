package com.sophimp.are.window

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sophimp.are.Constants
import com.sophimp.are.R
import com.sophimp.are.databinding.PopupWindowColorBinding

/**
 *
 * @author: sfx
 * @since: 2021/7/29
 */
class ColorPickerWindow(context: Context) : PopupWindow(context) {
    lateinit var colors: IntArray

    var pickerListener: PickerListener? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        colors = context.resources.getIntArray(R.array.colorPickerColors)
        val binding = PopupWindowColorBinding.inflate(LayoutInflater.from(context))
        contentView = binding.root
        binding.rvPalette.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        binding.rvPalette.adapter = ColorAdapter()
    }

    inner class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ColorHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
            val layoutParams = RecyclerView.LayoutParams((parent.context.resources.displayMetrics.density * 30).toInt(),
                (parent.context.resources.displayMetrics.density * 30).toInt())
            val view = TextView(parent.context)
            layoutParams.leftMargin = (parent.context.resources.displayMetrics.density * 5).toInt()
            layoutParams.rightMargin = (parent.context.resources.displayMetrics.density * 5).toInt()
            layoutParams.topMargin = (parent.context.resources.displayMetrics.density * 5).toInt()
            layoutParams.bottomMargin = (parent.context.resources.displayMetrics.density * 5).toInt()
            view.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            view.layoutParams = layoutParams
            return ColorHolder(view)
        }

        override fun getItemCount(): Int {
            return this@ColorPickerWindow.colors.size
        }

        override fun onBindViewHolder(holder: ColorHolder, position: Int) {
//            Util.log("colors size: ${this@ColorPickerWindow.colors.size} ${this@ColorPickerWindow.colors.contentToString()}")
            if (this@ColorPickerWindow.colors.isNotEmpty()) {
                if (position == 0) {
                    (holder.itemView as TextView).apply {
                        text = "def"
                        setBackgroundColor(Color.WHITE)
                    }
                } else {
                    val bg = GradientDrawable()
                    bg.cornerRadius = holder.itemView.context.resources.displayMetrics.density * 40
                    val color = this@ColorPickerWindow.colors[position]
                    bg.colors = intArrayOf(color, color)
                    (holder.itemView as TextView).apply {
                        text = ""
                        background = bg
                    }
                }
            }
        }

        inner class ColorHolder(view: View) : RecyclerView.ViewHolder(view) {
            init {
                view.setOnClickListener {
                    if (adapterPosition == 0) {
                        this@ColorPickerWindow.pickerListener?.onPickValue(Constants.DEFAULT_FEATURE)
                    } else {
                        this@ColorPickerWindow.pickerListener?.onPickValue(this@ColorPickerWindow.colors[adapterPosition])
                    }
                }
            }
        }
    }
}