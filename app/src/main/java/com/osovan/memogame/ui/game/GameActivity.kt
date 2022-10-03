package com.osovan.memogame.ui.game

import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.imageview.ShapeableImageView
import com.osovan.memogame.R
import com.osovan.memogame.databinding.ActivityGameBinding
import com.osovan.memogame.ui.dialog.Dialog
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

          setProgressBar(gameMode)
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
               gameViewModel.managePairs(binding.glTable)
          }

          gameViewModel.cardImages.observe(this) { images ->
               setImages(images)
          }

          gameViewModel.gameMoves.observe(this) { moves ->
               binding.tvMoves.text = resources.getString(R.string.moves, moves)
          }

          gameViewModel.gamePairs.observe(this) { pairs ->
               binding.tvPairs.text = resources.getString(R.string.pairs, pairs)
          }

          gameViewModel.gameTimer.observe(this) { progress ->
               binding.pbTime.progress = progress.toInt()
          }

          gameViewModel.endGame.observe(this) { endMode ->
               lateinit var title: String
               lateinit var message: String
               when (endMode) {
                    0 -> {
                         title = getString(R.string.dialog_title_lose)
                         message = getString(R.string.dialog_message_lose)
                    }
                    1 -> {
                         title = getString(R.string.dialog_title_win)
                         message = getString(R.string.dialog_message_win)
                    }
               }
               Dialog(
                    title = title,
                    message = message,
                    positiveButton = "volver al menú",
                    onSubmitClickListener = {
                         super.onBackPressed()
                    },
                    isEndDialog = true
               ).show(supportFragmentManager, "endDialog")
          }

          gameViewModel.onCreate(gameMode, gameTheme)

     }

     private fun getModeFromIntent() {
          gameMode = intent.extras!!.getInt(GAMEMODE_KEY)
     }

     private fun getThemeFromIntent() {
          gameTheme = intent.extras!!.getInt(GAMETHEME_KEY)
     }

     private fun setImages(images: MutableList<Int>) {
          for (i in 0 until binding.glTable.childCount) {
               val rlayout = binding.glTable.getChildAt(i) as RelativeLayout
               val imageView = rlayout.findViewWithTag<ShapeableImageView>("ivFront$i")
               gameViewModel.cards[i].image = images[i]
               imageView.setImageDrawable(
                    ResourcesCompat.getDrawable(
                         resources, images[i],
                         null
                    )
               )
          }
     }

     private fun setProgressBar(gameMode: Int) {
          val valueBar: Int = when (gameMode) {
               0 -> 60000
               1 -> 90000
               else -> 120000
          }
          binding.pbTime.max = valueBar
          binding.pbTime.progress = valueBar
     }

     override fun onBackPressed() {

          if (gameViewModel.gameStarted) {
               gameViewModel.pauseTimer()
          }
          Dialog(
               title = getString(R.string.dialog_title_back),
               message = getString(R.string.dialog_message_back),
               positiveButton = getString(R.string.button_yes),
               onSubmitClickListener = {
                    super.onBackPressed()
               },
               negativeButton = getString(R.string.button_no),
               onDismissClickListener = {
                    if (gameViewModel.gameStarted) {
                         gameViewModel.resumeTimer()
                    }
               }
          ).show(supportFragmentManager, "leaveDialog")
     }

     override fun onPause() {
          super.onPause()
          onBackPressed()
     }

}