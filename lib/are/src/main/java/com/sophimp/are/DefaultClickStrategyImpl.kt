package com.sophimp.are

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.util.Log
import com.sophimp.are.spans.UrlSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/29
 */
class DefaultClickStrategyImpl : IEditorClickStrategy {
    override fun onClickUrl(context: Context?, urlSpan: UrlSpan?): Boolean {
        val uri = Uri.parse(urlSpan?.url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context?.packageName)
        try {
            context?.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.w("URLSpan", "Actvity was not found for intent, $intent")
        }
        return true
    }
}