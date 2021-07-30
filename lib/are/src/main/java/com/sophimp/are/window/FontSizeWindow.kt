package com.sophimp.are.window

import android.content.Context
import android.view.*
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
class FontSizeWindow(context: Context) : PopupWindow(context) {
    lateinit var binding: PopupWindowColorBinding
    var fontSizes = Array(20) { i -> 17 + i }
        set(value) {
            field = value
            binding.rvPalette.adapter?.notifyDataSetChanged()
        }

    var pickerListener: PickerListener? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        binding = PopupWindowColorBinding.inflate(LayoutInflater.from(context))
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
            view.gravity = Gravity.CENTER
            view.layoutParams = layoutParams
            view.setBackgroundResource(R.drawable.shape_board_bg)
            return ColorHolder(view)
        }

        override fun getItemCount(): Int {
            return this@FontSizeWindow.fontSizes.size
        }

        override fun onBindViewHolder(holder: ColorHolder, position: Int) {
            if (this@FontSizeWindow.fontSizes.isNotEmpty()) {
                (holder.itemView as TextView).apply {
                    text = if (position == 0) "def" else "${this@FontSizeWindow.fontSizes[position]}"
                }
            }
        }

        inner class ColorHolder(view: View) : RecyclerView.ViewHolder(view) {
            init {
                view.setOnClickListener {
                    if (adapterPosition == 0) {
                        this@FontSizeWindow.pickerListener?.onPickValue(Constants.DEFAULT_FEATURE)
                    } else {
                        this@FontSizeWindow.pickerListener?.onPickValue(this@FontSizeWindow.fontSizes[adapterPosition])
                    }
                }
            }
        }
    }
}