package com.osovan.memogame.ui.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.osovan.memogame.R
import com.osovan.memogame.databinding.ActivityGameBinding
import com.osovan.memogame.utils.Constants.Companion.GAMEMODE_KEY
import com.osovan.memogame.utils.TAG

class GameActivity : AppCompatActivity() {

     private lateinit var binding: ActivityGameBinding
     private var gameMode = 0
     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          binding = ActivityGameBinding.inflate(layoutInflater)
          setContentView(binding.root)

          getModefromIntent()
          Log.d(TAG, "onCreate: gameMode = $gameMode")
     }

     private fun getModefromIntent() {
          gameMode = intent.extras!!.getInt(GAMEMODE_KEY)
     }
}