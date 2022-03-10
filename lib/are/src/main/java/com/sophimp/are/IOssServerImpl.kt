package com.sophimp.are

import android.text.TextUtils
import com.sophimp.are.listener.IOssServer

/**
 *  oss server helper impl
 * create by sfx on 2022/3/9 17:19
 */
class IOssServerImpl : IOssServer {
    override fun isServerPath(path: String?): Boolean {
        if (TextUtils.isEmpty(path)) return false
        return true
    }

    override fun getMemoAndDiaryImageUrl(url: String?): String {
        return url ?: ""
    }

    override fun obtainOssPrefixByType(type: String): String {
        return ""
    }
}