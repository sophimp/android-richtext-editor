package com.sophimp.are.listener

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Browser
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.sophimp.are.IEditorClickStrategy
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.*
import com.sophimp.are.table.EditTableDialog

/**
 * create by sfx on 2022/3/8 14:57
 */
class MemoEditorClickListener : IEditorClickStrategy {

    override fun onClickUrl(editText: RichEditText, urlSpan: UrlSpan?): Boolean {
        val uri = Uri.parse(urlSpan?.url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, editText.context?.packageName)
        try {
            editText.context?.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.w("URLSpan", "Actvity was not found for intent, $intent")
        }
        return true
    }

    override fun onClickImage(editText: RichEditText, imageSpan: ImageSpan2?): Boolean {

        return false
    }

    override fun onClickAudio(editText: RichEditText, audioSpan: AudioSpan?): Boolean {
        return false
    }

    override fun onClickVideo(editText: RichEditText, videoSpan: VideoSpan?): Boolean {
        return false
    }

    override fun onClickAttachment(
        editText: RichEditText,
        attachmentSpan: AttachmentSpan?
    ): Boolean {
        return false
    }

    override fun onClickTable(editText: RichEditText, tableSpan: TableSpan?): Boolean {
        tableSpan?.let {
            val dialog = EditTableDialog(null)
            dialog.setHtml(tableSpan.html)
            dialog.isCancelable = false
//            dialog.setCanceledOnTouchOutside(false)
            dialog.setConfirmListener { bitmap: Bitmap?, html: String? ->
//                replaceSpan(
//                    bitmap,
//                    html,
//                    tableSpan
//                )
            }
            dialog.show((editText.context as FragmentActivity).supportFragmentManager, "rich_table")
        }
        return true
    }
}