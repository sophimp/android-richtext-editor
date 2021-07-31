package com.sophimp.are.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sophimp.are.R
import com.sophimp.are.Util.toast
import com.sophimp.are.models.MediaInfo
import java.io.File
import java.util.*

/**
 * @author: sfx
 * @since: 2021/7/30
 */
class VideoAndImageGallery : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "ImageSelector"
        private const val PERMISSIONS_REQUEST_STORAGE_CODE = 197
        private const val PERMISSIONS_REQUEST_CAMERA_CODE = 341
        private const val CAMERA_REQUEST_CODE = 694
        private val localVideoThumbnailColumns = arrayOf(
            MediaStore.Video.Thumbnails.DATA,  // 视频缩略图路径
            MediaStore.Video.Thumbnails.VIDEO_ID,  // 视频id
            MediaStore.Video.Thumbnails.KIND,
            MediaStore.Video.Thumbnails.WIDTH,  // 视频缩略图宽度
            MediaStore.Video.Thumbnails.HEIGHT // 视频缩略图高度
        )
    }

    private val mColumnCount = 3

    // custom action bars
    private var mButtonBack: ImageView? = null
    private var picNum: TextView? = null
    private val mQueryMediaInfos: MutableList<MediaInfo> = ArrayList()
    private val mSelectMediaInfos: MutableList<MediaInfo> = ArrayList()
    private var rvMedia: RecyclerView? = null
    private var tvBtnPreview: TextView? = null
    private var tvBtnFullSize: TextView? = null
    private var tvPickCount: TextView? = null
    private var tvBtnComplete: TextView? = null
    private var mediaSelectAdapter: MediaSelectAdapter? = null
    private val takePicture = false
    private val mFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_and_video_gallery)
        initViews()
        initDatas()
    }

    fun requestReadStorageRuntimePermission() {
        if (ContextCompat.checkSelfPermission(this@VideoAndImageGallery, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@VideoAndImageGallery,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_STORAGE_CODE)
        } else {
            LoadVideoAndImages()
        }
    }

    fun requestCameraRuntimePermissions() {
        if (ContextCompat.checkSelfPermission(this@VideoAndImageGallery,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this@VideoAndImageGallery,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this@VideoAndImageGallery,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@VideoAndImageGallery,
                arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_CAMERA_CODE)
        } else {
            jumpToCameraCapture()
        }
    }

    private fun jumpToCameraCapture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoUri = Uri.fromFile(File(mFilePath)) // 传递路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri) // 更改系统默认存储路径
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_STORAGE_CODE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    LoadVideoAndImages()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Toast.makeText(ImagesSelectorActivity.this, getString(R.string.selector_permission_error), Toast.LENGTH_SHORT).show();
                }
                return
            }
            PERMISSIONS_REQUEST_CAMERA_CODE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    jumpToCameraCapture()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Toast.makeText(ImagesSelectorActivity.this, getString(R.string.selector_permission_error), Toast.LENGTH_SHORT).show();
                }
                return
            }
        }
    }

    private val imageProjections = arrayOf(
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media._ID)
    private val videoProjections = arrayOf(
        MediaStore.Video.Media._ID,  // 视频id
        MediaStore.Video.Media.DATA,  // 视频路径
        MediaStore.Video.Media.SIZE,  // 视频字节大小
        MediaStore.Video.Media.DISPLAY_NAME,  // 视频名称 xxx.mp4
        MediaStore.Video.Media.TITLE,  // 视频标题
        MediaStore.Video.Media.DATE_ADDED,  // 视频添加到MediaProvider的时间
        MediaStore.Video.Media.DATE_MODIFIED,  // 上次修改时间，该列用于内部MediaScanner扫描，外部不要修改
        MediaStore.Video.Media.MIME_TYPE,  // 视频类型 video/mp4
        MediaStore.Video.Media.DURATION,  // 视频时长
        MediaStore.Video.Media.ARTIST,  // 艺人名称
        MediaStore.Video.Media.ALBUM,  // 艺人专辑名称
        MediaStore.Video.Media.RESOLUTION,  // 视频分辨率 X x Y格式
        MediaStore.Video.Media.DESCRIPTION,  // 视频描述
        MediaStore.Video.Media.IS_PRIVATE,
        MediaStore.Video.Media.TAGS,
        MediaStore.Video.Media.CATEGORY,  // YouTube类别
        MediaStore.Video.Media.LANGUAGE,  // 视频使用语言
        MediaStore.Video.Media.LATITUDE,  // 拍下该视频时的纬度
        MediaStore.Video.Media.LONGITUDE,  // 拍下该视频时的经度
        MediaStore.Video.Media.DATE_TAKEN,
        MediaStore.Video.Media.MINI_THUMB_MAGIC,
        MediaStore.Video.Media.BUCKET_ID,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Video.Media.BOOKMARK // 上次视频播放的位置
    )

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // after capturing image, return the image path as selected result
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val save_path = data!!.getStringExtra("save_path")
                if (!TextUtils.isEmpty(save_path)) {
                    // notify system
                    val file = File(save_path)
                    sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
                    if (file.exists()) {
                        if (file.length() <= 0) {
                            toast(this, "文件无效")
                            return
                        }
                    }
                    val resultIntent = Intent()
                    //                    resultIntent.putStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS, ImageListContent.SELECTED_IMAGES);
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    protected fun initViews() {
        rvMedia = findViewById(R.id.rv_medias)
        tvBtnPreview = findViewById(R.id.tv_btn_preview)
        tvBtnPreview?.setOnClickListener(this)
        tvBtnFullSize = findViewById(R.id.tv_btn_full_size)
        tvBtnFullSize?.setOnClickListener(this)
        tvPickCount = findViewById(R.id.tv_pick_count)
        tvPickCount?.setOnClickListener(this)
        tvBtnComplete = findViewById(R.id.tv_btn_complete)
        tvBtnComplete?.setOnClickListener(this)
    }

    protected fun initDatas() {
        requestReadStorageRuntimePermission()
        rvMedia!!.itemAnimator = null
        rvMedia!!.layoutManager = GridLayoutManager(this, mColumnCount)
        mediaSelectAdapter = MediaSelectAdapter(null)
        rvMedia!!.adapter = mediaSelectAdapter
    }

    protected fun setListenner() {
        mediaSelectAdapter!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val mediaInfo = mediaSelectAdapter!!.datas[position]
            if (mediaInfo.isCamera) {
                requestCameraRuntimePermissions()
            } else {
                mediaInfo.isSelected = !mediaInfo.isSelected
                if (mediaInfo.isSelected) {
                    mediaInfo.isSelected = !mediaInfo.isSelected
                    mSelectMediaInfos.add(mediaInfo)
                } else {
                    mSelectMediaInfos.remove(mediaInfo)
                }
                tvPickCount!!.text = mSelectMediaInfos.size.toString() + ""
                mediaSelectAdapter!!.notifyItemChanged(position)
            }
        }
    }

    // this method is to load images and folders for all
    fun LoadVideoAndImages() {
        // todo coroutine
        queryImages()
        queryVideos()
        Collections.sort(mQueryMediaInfos) { o1, o2 -> (o2.dateAdded - o1.dateAdded).toInt() }
        val images = ArrayList<List<MediaInfo>>()
        images.add(mQueryMediaInfos)
        var displayName = ""
        displayName = if (takePicture) "拍视频" else "拍照"
        mQueryMediaInfos.add(0, MediaInfo(true, displayName))
        mediaSelectAdapter!!.datas = mQueryMediaInfos
    }

    private fun queryVideos() {
        val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"
        val cursor = contentResolver.query(contentUri, videoProjections,
            null,
            null, sortOrder)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val mediaInfo = MediaInfo()
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                val dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))
                val dateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED))
                val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE))
                val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST))
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ALBUM))
                val resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION))
                val description = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DESCRIPTION))
                val isPrivate = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.IS_PRIVATE))
                val tags = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TAGS))
                val category = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.CATEGORY))
                val latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Video.Media.LATITUDE))
                val longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Video.Media.LONGITUDE))
                val dateTaken = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN))
                val miniThumbMagic = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.MINI_THUMB_MAGIC))
                val bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
                val bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                val bookmark = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.BOOKMARK))
                val thumbnailCursor = contentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    localVideoThumbnailColumns,
                    MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id,
                    null,
                    null)
                if (thumbnailCursor != null && thumbnailCursor.moveToFirst()) {
                    do {
                        val thumbnailData = thumbnailCursor.getString(thumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA))
                        val kind = thumbnailCursor.getInt(thumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.KIND))
                        val width = thumbnailCursor.getLong(thumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.WIDTH))
                        val height = thumbnailCursor.getLong(thumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.HEIGHT))
                        mediaInfo.thumbnailData = thumbnailData
                        mediaInfo.kind = kind
                        mediaInfo.width = width
                        mediaInfo.height = height
                    } while (thumbnailCursor.moveToNext())
                    thumbnailCursor.close()
                }
                mediaInfo.mediaInfoType = MediaInfo.Type.VIDEO
                mediaInfo.id = id
                mediaInfo.data = data
                mediaInfo.size = size
                mediaInfo.displayName = displayName
                mediaInfo.title = title
                mediaInfo.dateAdded = dateAdded
                mediaInfo.dateModified = dateModified
                mediaInfo.mimeType = mimeType
                mediaInfo.duration = duration
                mediaInfo.artist = artist
                mediaInfo.album = album
                mediaInfo.resolution = resolution
                mediaInfo.description = description
                mediaInfo.isPrivate = isPrivate
                mediaInfo.tags = tags
                mediaInfo.category = category
                mediaInfo.latitude = latitude
                mediaInfo.longitude = longitude
                mediaInfo.dateTaken = dateTaken
                mediaInfo.miniThumbMagic = miniThumbMagic
                mediaInfo.bucketId = bucketId
                mediaInfo.bucketDisplayName = bucketDisplayName
                mediaInfo.bookmark = bookmark

//                LogUtils.d("sfx mediaInfo = " + mediaInfo.toString());
                mQueryMediaInfos.add(mediaInfo)
            } while (cursor.moveToNext())
            cursor.close()
        }
    }

    private fun queryImages() {
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"
        val cursor = contentResolver.query(contentUri,
            imageProjections,
            MediaStore.Images.Media.MIME_TYPE + "=? or "
                    + MediaStore.Images.Media.MIME_TYPE + "=? or "
                    + MediaStore.Images.Media.MIME_TYPE + "=? or "
                    + MediaStore.Images.Media.MIME_TYPE + "=?",
            arrayOf("image/jpeg", "image/png", "image/jpg", "image/x-ms-bmp"),
            sortOrder)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val mediaInfo = MediaInfo()
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                val dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))
                mediaInfo.mediaInfoType = MediaInfo.Type.IMAGE
                mediaInfo.id = id
                mediaInfo.data = data
                mediaInfo.size = size
                mediaInfo.displayName = displayName
                mediaInfo.dateAdded = dateAdded
                mQueryMediaInfos.add(mediaInfo)
                //                LogUtils.d("sfx mediaInfo = " + mediaInfo.toString());
            } while (cursor.moveToNext())
            cursor.close()
        }
    }

    override fun onClick(v: View) {
//        if (v.id == R.id.selector_button_back || v.id == R.id.pic_num) {
//            val data = Intent()
//            data.putStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS, ArrayList())
//            setResult(Activity.RESULT_OK, data)
//            finish()
//        } else if (v.id == R.id.tv_btn_complete) { // 完成按钮
//            // 取消按钮
//            val data = Intent()
//            if (mSelectMediaInfos.size > 0) {
//                val selectPath = ArrayList<String?>()
//                for (mediaInfo in mSelectMediaInfos) {
//                    if (!TextUtils.isEmpty(mediaInfo.data)) {
//                        selectPath.add(mediaInfo.data)
//                    }
//                }
//                data.putStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS, selectPath)
//                setResult(Activity.RESULT_OK, data)
//            }
//            finish()
//        } else if (v.id == R.id.tv_btn_full_size) {
//            tvBtnFullSize!!.isSelected = !tvBtnFullSize!!.isSelected
//        }
    }

}