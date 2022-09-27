package com.osovan.memogame.ui.InitMenu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.osovan.memogame.R
import com.osovan.memogame.databinding.ActivityMainBinding
import com.osovan.memogame.utils.TAG

class MainActivity : AppCompatActivity() {

     private lateinit var binding: ActivityMainBinding

     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          binding = ActivityMainBinding.inflate(layoutInflater)
          setContentView(binding.root)

          binding.apply {
               btnEasy.setOnClickListener {
                    Log.d(TAG, "btnEasy: gameType = 0")

               }
               btnMedium.setOnClickListener {
                    Log.d(TAG, "btnMedium: gameType = 1")
               }
               btnHard.setOnClickListener {
                    Log.d(TAG, "btnHard: gameType = 2")
               }
          }
     }
}