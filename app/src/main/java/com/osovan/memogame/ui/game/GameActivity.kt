package com.osovan.memogame.ui.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.osovan.memogame.R
import com.osovan.memogame.databinding.ActivityGameBinding
import com.osovan.memogame.utils.Constants.Companion.GAMEMODE_KEY
import com.osovan.memogame.utils.Constants.Companion.GAMETHEME_KEY
import com.osovan.memogame.utils.TAG

class GameActivity : AppCompatActivity() {

     private lateinit var binding: ActivityGameBinding
     private val gameViewModel: GameViewModel by viewModels()
     private var gameMode = 0
     private var gameTheme = 0
     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          binding = ActivityGameBinding.inflate(layoutInflater)
          setContentView(binding.root)

          getModeFromIntent()
          getThemeFromIntent()
          Log.d(TAG, "onCreate: gameMode = $gameMode")

          gameViewModel.gameBoardSize.observe(this) { size ->
               binding.glTable.apply {
                    columnCount = size.first()
                    rowCount = size.last()
               }
          }

          gameViewModel.gameBoardViews.observe(this) { views ->
               views.forEach {
                    binding.glTable.addView(it)
               }
          }

          gameViewModel.onCreate(gameMode, gameTheme)

     }

     private fun getModeFromIntent() {
          gameMode = intent.extras!!.getInt(GAMEMODE_KEY)
     }

     private fun getThemeFromIntent() {
          gameTheme = intent.extras!!.getInt(GAMETHEME_KEY)
     }
}