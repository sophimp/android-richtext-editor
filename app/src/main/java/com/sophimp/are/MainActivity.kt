package com.sophimp.are

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sophimp.are.databinding.ActivityMainBinding

/**
 *
 * @author: sfx
 * @since: 2021/8/4
 */
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}