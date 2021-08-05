package com.sophimp.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sophimp.are.Util
import com.sophimp.are.databinding.ActivityEditBinding
import com.sophimp.demo.db.MemoDatabase
import com.sophimp.demo.db.MemoInfo

class EditActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditBinding
    var memoDao = MemoDatabase.instance.getMemoDao()
    var memoInfo = MemoInfo("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.initDefaultToolItem(binding.reRichtext)
        val id = intent.getLongExtra("id", -1L)
        if (id != -1L) {
            memoInfo = memoDao.queryMemoById(id)
            binding.reRichtext.fromHtml(memoDao.queryMemoById(id).richText)
        }
    }

    override fun onPause() {
        Util.log("edit onPause: ${binding.reRichtext.toHtml()}")
        memoInfo.richText = binding.reRichtext.toHtml()
        memoDao.addMemo(memoInfo)
        super.onPause()
    }
}