package com.sophimp.are.models

/**
 * video and image info
 */
class MediaInfo {
    enum class Type {
        IMAGE, VIDEO
    }

    var id = 0

    /**
     * path
     */
    var data: String? = null

    /**
     * name
     */
    var displayName: String? = null

    /**
     * 共用时间
     */
    var dateAdded: Long = 0

    /**
     * 根据查询结果手动赋值
     */
    var mediaInfoType: Type? = null
    var size: Long = 0
    var title: String? = null
    var dateModified: Long = 0
    var mimeType: String? = null
    var duration: Long = 0
    var artist: String? = null
    var album: String? = null
    var resolution: String? = null
    var description: String? = null
    var isPrivate = 0
    var tags: String? = null
    var category: String? = null
    var latitude = 0.0
    var longitude = 0.0
    var dateTaken = 0
    var miniThumbMagic = 0
    var bucketId: String? = null
    var bucketDisplayName: String? = null
    var bookmark = 0
    var isSelected = false
    var thumbnailData: String? = null
    var kind = 0
    var width: Long = 0
    var height: Long = 0
    var isCamera = false

    constructor() {}
    constructor(isCamera: Boolean, displayName: String?) {
        this.isCamera = isCamera
        this.displayName = displayName
    }

    override fun toString(): String {
        return "VideoInfo{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", size=" + size +
                ", displayName='" + displayName + '\'' +
                ", title='" + title + '\'' +
                ", dateAdded=" + dateAdded +
                ", dateModified=" + dateModified +
                ", mimeType='" + mimeType + '\'' +
                ", duration=" + duration +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", resolution='" + resolution + '\'' +
                ", description='" + description + '\'' +
                ", isPrivate=" + isPrivate +
                ", tags='" + tags + '\'' +
                ", category='" + category + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", dateTaken=" + dateTaken +
                ", miniThumbMagic=" + miniThumbMagic +
                ", bucketId='" + bucketId + '\'' +
                ", bucketDisplayName='" + bucketDisplayName + '\'' +
                ", bookmark=" + bookmark +
                ", thumbnailData='" + thumbnailData + '\'' +
                ", kind=" + kind +
                ", width=" + width +
                ", height=" + height +
                '}'
    }
}