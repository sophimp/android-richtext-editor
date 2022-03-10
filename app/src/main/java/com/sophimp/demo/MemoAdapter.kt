package com.sophimp.demo

import android.content.Intent
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sophimp.are.demo.R
import com.sophimp.are.demo.databinding.ItemMemoBinding
import com.sophimp.are.inner.Html
import com.sophimp.demo.db.MemoInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author: sfx
 * @since: 2021/8/4
 */
class MemoAdapter(var data: MutableList<MemoInfo>) : RecyclerView.Adapter<MemoAdapter.MemoHolder>() {

    val jobScope = CoroutineScope(Dispatchers.IO)

    fun setNewData(d: MutableList<MemoInfo>) {
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
        var spannableStringBuilder: SpannableStringBuilder
        val parseJob = jobScope.launch {
            spannableStringBuilder = Html.fromHtml(data[position].title, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH) as SpannableStringBuilder
            withContext(Dispatchers.Main) {
                holder.binding.richText.text = spannableStringBuilder
            }
        }
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