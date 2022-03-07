package com.sophimp.demo

import android.app.Application
import android.content.Context
import com.sophimp.are.IOssServer
import com.sophimp.are.utils.Util

/**
 *
 * @author: sfx
 * @since: 2021/8/5
 */
class AppApplication : Application() {
    companion object {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Util.initEnv(applicationContext, object : IOssServer {
            override fun isServerPath(path: String?): Boolean {
                return false
            }

            override fun getMemoAndDiaryImageUrl(url: String?): String {
                return ""
            }

            override fun obtainOssPrefixByType(type: String): String {
                return ""
            }

        })
    }
}