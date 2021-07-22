package com.sophimp.are.style.windows

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.sophimp.are.R
import com.sophimp.are.Util.toast
import com.sophimp.are.toolbar.items.ImageToolItem

class ImageSelectDialog(
    private val mContext: Context,
    areImage: ImageToolItem?,
    private val mRequestCode: Int
) {
    private val mRootView: View
    private val mDialog: Dialog
    private fun initView(): View {
        val layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater.inflate(R.layout.are_image_select, null)
        val insertInternetImageLayout =
            view.findViewById<RelativeLayout>(R.id.are_image_select_from_internet_layout)
        val radioGroup =
            view.findViewById<RadioGroup>(R.id.are_image_select_radio_group)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.are_image_select_from_local) {
                openImagePicker()
            } else {
                insertInternetImageLayout.visibility = View.VISIBLE
            }
        }
        val insertInternetImage =
            view.findViewById<TextView>(R.id.are_image_select_insert)
        insertInternetImage.setOnClickListener { insertInternetImage() }
        return view
    }

    fun show() {
        mDialog.show()
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        (mContext as Activity).startActivityForResult(intent, mRequestCode)
        mDialog.dismiss()
    }

    private fun insertInternetImage() {
        val editText =
            mRootView.findViewById<EditText>(R.id.are_image_select_internet_image_url)
        val imageUrl = editText.text.toString()
        if (imageUrl.startsWith("http")
            && (imageUrl.endsWith("png") || imageUrl.endsWith("jpg") || imageUrl.endsWith("jpeg"))
        ) {
//            .insertImage(imageUrl, AreImageSpan.ImageType.URL);
            mDialog.dismiss()
        } else {
            toast(mContext, "Not a valid image")
        }
    }

    init {
        mRootView = initView()
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Insert Image")
        builder.setView(mRootView)
        mDialog = builder.create()
    }
}