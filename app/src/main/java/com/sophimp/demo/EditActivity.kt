package com.sophimp.demo

import android.os.Bundle
import android.text.Spanned
import android.text.TextUtils
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.sophimp.are.demo.databinding.ActivityEditBinding
import com.sophimp.are.inner.Html
import com.sophimp.are.utils.Util
import com.sophimp.demo.db.MemoDatabase
import com.sophimp.demo.db.MemoInfo
import kotlin.math.min

class EditActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditBinding
    var memoDao = MemoDatabase.instance.getMemoDao()
    var memoInfo = MemoInfo("", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.toolbar.initDefaultToolItem(binding.reRichtext)
        val id = intent.getLongExtra("id", -1L)
        if (id != -1L) {
            memoInfo = memoDao.queryMemoById(id)
            binding.reRichtext.fromHtml(memoInfo.richText)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        Util.log("edit onPause: ${binding.reRichtext.toHtml()}")
        memoInfo.title = Html.toHtml(binding.reRichtext.editableText.subSequence(0, min(1000, binding.reRichtext.length())) as Spanned?,
            Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
        memoInfo.richText = binding.reRichtext.toHtml()
        if (!TextUtils.isEmpty(memoInfo.richText)) {
            memoDao.addMemo(memoInfo)
        }
        super.onPause()
    }
}