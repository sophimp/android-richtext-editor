package com.sophimp.are

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sophimp.are.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.initDefaultToolItem(binding.reRichtext)
    }
}