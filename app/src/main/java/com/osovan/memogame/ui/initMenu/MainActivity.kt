package com.osovan.memogame.ui.initMenu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import com.osovan.memogame.App.MemoGameApp.Companion.mPrefs
import com.osovan.memogame.R
import com.osovan.memogame.databinding.ActivityMainBinding
import com.osovan.memogame.ui.game.GameActivity
import com.osovan.memogame.utils.Constants.Companion.GAMEMODE_KEY
import com.osovan.memogame.utils.Constants.Companion.GAMETHEME_KEY
import com.osovan.memogame.utils.TAG

class MainActivity : AppCompatActivity() {

     private lateinit var binding: ActivityMainBinding

     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          binding = ActivityMainBinding.inflate(layoutInflater)
          setContentView(binding.root)

          binding.apply {

               var themeImages = 0

               rgTheme.setOnCheckedChangeListener { radioGroup, i ->
                    val radio: RadioButton = findViewById(i)

                    themeImages = with(radio.text) {
                         when {
                              contains("Ricky") -> 0
                              contains("Pokemon") -> 1
                              else -> 0
                         }
                    }

               }


               btnEasy.setOnClickListener {
                    initGameActivity(0, themeImages)
               }
               btnMedium.setOnClickListener {
                    initGameActivity(1, themeImages)
               }
               btnHard.setOnClickListener {
                    initGameActivity(2, themeImages)
               }
          }
     }

     private fun initGameActivity(gameMode: Int, gameTheme: Int) {
          Log.d(TAG, "New Game. Game Mode: $gameMode, Game Theme: $gameTheme")

          val intent = Intent(this, GameActivity::class.java)
          intent.apply {
               putExtra(GAMEMODE_KEY, gameMode)
               putExtra(GAMETHEME_KEY, gameTheme)
          }
          startActivity(intent)
     }

     private fun updateWins() {
          binding.tvVictories.text = resources.getString(R.string.current_wins, mPrefs.getWins())
     }

     override fun onResume() {
          super.onResume()
          updateWins()
     }
}