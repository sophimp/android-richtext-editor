package com.sophimp.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sophimp.are.Util
import com.sophimp.are.demo.databinding.ActivityMainBinding
import com.sophimp.demo.db.MemoDao
import com.sophimp.demo.db.MemoDatabase

/**
 *
 * @author: sfx
 * @since: 2021/8/4
 */
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var memoDao: MemoDao = MemoDatabase.instance.getMemoDao()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }

        binding.rvMemoList.layoutManager = LinearLayoutManager(this)
        binding.rvMemoList.adapter = MemoAdapter(memoDao.queryMemoAll())
    }

    override fun onResume() {
        Util.log("main onResume")
        (binding.rvMemoList.adapter as MemoAdapter).setNewData(memoDao.queryMemoAll())
        super.onResume()
    }
}