package com.sophimp.are.spans

/**
 * @author: sfx
 * @since: 2021/7/20
 */
interface IUploadSpan {
    fun uploadPath(): String?
    fun uploadFileSize(): Int?
}