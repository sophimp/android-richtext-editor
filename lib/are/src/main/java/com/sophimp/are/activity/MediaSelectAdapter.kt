package com.sophimp.are.activity

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sophimp.are.R
import com.sophimp.are.activity.MediaSelectAdapter.MediaSelectViewHolder
import com.sophimp.are.models.MediaInfo
import java.io.File
import java.util.*

/**
 * video and image gallery
 */
class MediaSelectAdapter(data: List<MediaInfo>?) : RecyclerView.Adapter<MediaSelectViewHolder>() {

    var onItemClickListener: AdapterView.OnItemClickListener? = null
    var datas: List<MediaInfo> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        if (data != null) {
            datas = data
        }
    }

    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaSelectViewHolder {
        mContext = parent.context.applicationContext
        return MediaSelectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_media_recyclerview, parent, false))
    }

    override fun onBindViewHolder(holder: MediaSelectViewHolder, position: Int) {
        val item = datas[position]
        holder.mediaInfo = item
        val newURI: Uri
        if (!item.isCamera && !TextUtils.isEmpty(item.data)) {
            // draw image first
            val imageFile = File(item.data)
            newURI = if (imageFile.exists()) {
                Uri.fromFile(imageFile)
            } else {
                Uri.Builder().scheme("res").path(R.mipmap.default_image.toString()).build()
            }
            Glide.with(mContext!!)
                .load(newURI)
                .placeholder(R.mipmap.default_image)
                .into(holder.ivBg)
            if (item.mediaInfoType == MediaInfo.Type.IMAGE) {
                holder.ivCamera.visibility = View.GONE
            } else {
                holder.ivCamera.visibility = View.VISIBLE
                holder.ivCamera.setImageResource(R.mipmap.icon_video_play)
            }
            holder.ivImageChecked.isSelected = item.isSelected
        } else {
            holder.ivBg.setBackgroundColor(Color.parseColor("#e2e2e2"))
            holder.ivCamera.visibility = View.VISIBLE
            holder.ivCamera.setImageResource(R.mipmap.icon_camera)
        }
        holder.tvImageName.visibility = if (item.isCamera) View.VISIBLE else View.GONE
        holder.tvImageName.text = item.displayName
        holder.ivCamera.isClickable = !item.isCamera
        holder.ivImageChecked.visibility = if (item.isCamera) View.GONE else View.VISIBLE
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class MediaSelectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mediaInfo: MediaInfo? = null
        var ivBg: ImageView
        var ivCamera: ImageView
        var ivImageChecked: ImageView
        var tvImageName: TextView
        var imageMask: View
        var bgContainer: View

        init {
            ivBg = view.findViewById(R.id.iv_src)
            ivCamera = view.findViewById(R.id.iv_camera_src)
            ivImageChecked = view.findViewById(R.id.iv_image_checked)
            tvImageName = view.findViewById(R.id.tv_image_name)
            imageMask = view.findViewById(R.id.image_mask)
            bgContainer = view.findViewById(R.id.rl_src_container)
            view.setOnClickListener {
                onItemClickListener?.onItemClick(null, view, adapterPosition, 0)
            }
        }
    }

}