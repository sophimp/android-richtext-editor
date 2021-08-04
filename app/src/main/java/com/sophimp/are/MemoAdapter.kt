package com.sophimp.are

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sophimp.are.databinding.ItemMemoBinding
import com.sophimp.are.db.MemoInfo

/**
 * @author: sfx
 * @since: 2021/8/4
 */
class MemoAdapter(var data: List<MemoInfo>) : RecyclerView.Adapter<MemoAdapter.MemoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoHolder {
        return MemoHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MemoHolder, position: Int) {
        holder.binding.richText.fromHtml(data[position].richText)
    }

    inner class MemoHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ItemMemoBinding = ItemMemoBinding.bind(view)
    }

}