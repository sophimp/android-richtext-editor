package com.sophimp.are.listener

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.text.Spanned
import android.util.Log
import com.sophimp.are.IEditorClickStrategy
import com.sophimp.are.spans.*

/**
 * create by sfx on 2022/3/8 14:57
 */
class MemoEditorClickListener : IEditorClickStrategy {

    override fun onClickUrl(context: Context, editable: Spanned, urlSpan: UrlSpan?): Boolean {
        val uri = Uri.parse(urlSpan?.url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.w("URLSpan", "Actvity was not found for intent, $intent")
        }
        return true
    }

    override fun onClickImage(
        context: Context,
        editable: Spanned,
        imageSpan: ImageSpan2?
    ): Boolean {

        return false
    }

    override fun onClickAudio(context: Context, editable: Spanned, audioSpan: AudioSpan?): Boolean {

        return false
    }

    override fun onClickVideo(context: Context, editable: Spanned, videoSpan: VideoSpan?): Boolean {

        return false
    }

    override fun onClickAttachment(
        context: Context,
        editable: Spanned,
        attachmentSpan: AttachmentSpan?
    ): Boolean {

        return false
    }

    override fun onClickTable(context: Context, editable: Spanned, tableSpan: TableSpan?): Boolean {

        return false
    }
}