package com.sophimp.demo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sophimp.are.R
import com.sophimp.are.databinding.ItemMemoBinding
import com.sophimp.are.inner.Html
import com.sophimp.demo.db.MemoInfo

/**
 * @author: sfx
 * @since: 2021/8/4
 */
class MemoAdapter(var data: List<MemoInfo>) : RecyclerView.Adapter<MemoAdapter.MemoHolder>() {

    fun setNewData(d: List<MemoInfo>) {
        data = d
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoHolder {
        return MemoHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MemoHolder, position: Int) {
        holder.memoInfo = data[position]
        holder.binding.richText.text =
            Html.fromHtml(data[position].richText, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
    }

    inner class MemoHolder(view: View) : RecyclerView.ViewHolder(view) {
        var memoInfo: MemoInfo? = null
        var binding: ItemMemoBinding = ItemMemoBinding.bind(view)

        init {
            binding.root.setOnClickListener {
                val intent = Intent(it.context, EditActivity::class.java)
                intent.putExtra("id", memoInfo?.id)
                it.context.startActivity(intent)
            }
        }
    }

}