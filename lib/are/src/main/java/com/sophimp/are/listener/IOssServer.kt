package com.sophimp.are.listener

/**
 * create by sfx on 2022/3/2 18:13
 */
interface IOssServer {

    fun isServerPath(path: String?): Boolean
    fun getMemoAndDiaryImageUrl(url: String?): String

    fun obtainOssPrefixByType(type: String): String

}