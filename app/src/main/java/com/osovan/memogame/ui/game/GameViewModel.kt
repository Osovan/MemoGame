package com.osovan.memogame.ui.game

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.imageview.ShapeableImageView
import com.osovan.memogame.App.MemoGameApp.Companion.mAppContext
import com.osovan.memogame.App.MemoGameApp.Companion.mPrefs
import com.osovan.memogame.App.MemoGameApp.Companion.mResources
import com.osovan.memogame.R
import com.osovan.memogame.data.Card
import com.osovan.memogame.utils.TAG
import com.osovan.memogame.utils.loadBackAnimator
import com.osovan.memogame.utils.loadFrontAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Field
import kotlin.properties.Delegates

class GameViewModel : ViewModel() {
     val gameBoardSize = MutableLiveData<ArrayList<Int>>()
     val gameBoardViews = MutableLiveData<ArrayList<RelativeLayout>>()
     val cardImages = MutableLiveData<ArrayList<Int>>()
     val gamePairs = MutableLiveData<Int>()
     val gameMoves = MutableLiveData<Int>()
     val endGame = MutableLiveData<Int>()
     val gameTimer = MutableLiveData<Long>()


     var cards = mutableListOf<Card>()
     private var col by Delegates.notNull<Int>()
     private var boardSize by Delegates.notNull<Int>()
     private var pairs = 0
     private var moves = 0
     private val visibleCards = mutableListOf<Int>()
     private var timeLevel: Long = 0
     private lateinit var timer: CountDownTimer
     var currentTimerMillis = 0L
     var gameStarted = false
     private var isBlocked = false

     fun onCreate(gameMode: Int, gameTheme: Int) {
          viewModelScope.launch {
               withContext(Dispatchers.IO) {
                    gameBoardSize.postValue((setTableSize(gameMode)))
                    gamePairs.postValue(pairs)
                    gameBoardViews.postValue(createBoardViews())
                    cardImages.postValue(selectImages(getAllDrawables(gameTheme)))
                    gameMoves.postValue(moves)
               }
               timeLevel = when (gameMode) {
                    0 -> 60000
                    1 -> 90000
                    else -> 120000
               }
               setupTimer(timeLevel)
          }
     }

     /**
      * Función para crear el timer de la progressbar
      */
     private fun setupTimer(timeLevel: Long) {
          timer = object : CountDownTimer(timeLevel, 100) {
               override fun onTick(p0: Long) {
                    currentTimerMillis = p0
                    gameTimer.postValue(p0)
               }

               override fun onFinish() {
                    gameTimer.postValue(0)
                    endGame.postValue(0)
               }
          }
     }

     private fun startTimer() {
          timer.start()
     }

     fun pauseTimer() {
          timer.cancel()
     }

     fun resumeTimer() {
          timer = object : CountDownTimer(currentTimerMillis, 100) {
               override fun onTick(tick: Long) {
                    currentTimerMillis = tick
                    gameTimer.postValue(tick)
               }

               override fun onFinish() {
                    gameTimer.postValue(0)
                    endGame.postValue(0)
               }
          }.start()
     }

     /**
      * Función para crear la vista del tablero
      */
     private fun createBoardViews(): ArrayList<RelativeLayout> {
          //obtenemos el ancho de la pantalla
          val screenWidth = mResources.displayMetrics.widthPixels - (TypedValue.applyDimension(
               TypedValue.COMPLEX_UNIT_DIP,
               8F,
               mResources.displayMetrics
          ) * 2).toInt()


          val layouts = arrayListOf<RelativeLayout>()
          for (i in 0 until boardSize) {
               //Añadimos las cartas a cada objeto
               cards.add(Card(i))


               //Creamos un RelativeLayout según el ancho y alto calculado anteriormente
               //y lo divididomos entre las columnas que necesitemos
               val relativeLayout = RelativeLayout(mAppContext).apply {
                    val myLayoutParams = LinearLayout.LayoutParams(
                         screenWidth / col,
                         screenWidth / col
                    ).apply {
                         setPadding(8)
                    }
                    layoutParams = myLayoutParams
               }

               //Creamos un cardview con ShapeableImageView para hacer la tarjeta frontal
               val cardViewFront = CardView(mAppContext).apply {
                    val myLayoutParams = LinearLayout.LayoutParams(
                         LinearLayout.LayoutParams.MATCH_PARENT,
                         LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    layoutParams = myLayoutParams
                    radius = 12f
                    cardElevation = 0f
                    alpha = 0f
                    setCardBackgroundColor(Color.TRANSPARENT)
                    tag = "cvFront$i"
               }

               val imageViewFront = ShapeableImageView(mAppContext).apply {
                    val myLayoutParams = LinearLayout.LayoutParams(
                         LinearLayout.LayoutParams.MATCH_PARENT,
                         LinearLayout.LayoutParams.MATCH_PARENT
                    ).apply {
                         gravity = Gravity.CENTER
                    }
                    layoutParams = myLayoutParams
                    shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                         .setAllCornerSizes(12f)
                         .build()
                    strokeWidth = 2f
                    strokeColor = ColorStateList.valueOf(resources.getColor(R.color.black, null))
                    tag = "ivFront$i"
               }

               //hacemos lo mismo para la tarjeta trasera: Craemos un cardview con ShapeableImageView
               val cardViewBack = CardView(mAppContext).apply {
                    val myLayoutParams = LinearLayout.LayoutParams(
                         LinearLayout.LayoutParams.MATCH_PARENT,
                         LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    layoutParams = myLayoutParams
                    radius = 12f
                    cardElevation = 0F
                    tag = "cvBack$i"
                    setCardBackgroundColor(Color.TRANSPARENT)
               }

               val imageViewBack = ShapeableImageView(mAppContext).apply {
                    val layIVB = LinearLayout.LayoutParams(
                         LinearLayout.LayoutParams.MATCH_PARENT,
                         LinearLayout.LayoutParams.MATCH_PARENT
                    ).apply {
                         gravity = Gravity.CENTER
                    }
                    layoutParams = layIVB
                    shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                         .setAllCornerSizes(12f)
                         .build()
                    strokeWidth = 2f
                    strokeColor = ColorStateList.valueOf(resources.getColor(R.color.black, null))
                    tag = "ivBack$i"
                    setImageDrawable(
                         ResourcesCompat.getDrawable(
                              resources,
                              R.drawable.interrogante,
                              null
                         )
                    )
               }


               //Añadimos los ShapeableImageviews a los cardviews
               cardViewFront.addView(imageViewFront)
               cardViewBack.addView(imageViewBack)

               //Añadimos los cardViews al RelativeLayout, y este al arraylist
               relativeLayout.addView(cardViewFront)
               relativeLayout.addView(cardViewBack)
               layouts.add(relativeLayout)
          }

          return layouts


     }

     /**
      * Cálculo del tamaño del tablero
      */
     private fun setTableSize(gameMode: Int): ArrayList<Int> {
          var col = 0
          var row = 0

          when (gameMode) {
               0 -> {
                    col = 4
                    row = 4
               }
               1 -> {
                    col = 4
                    row = 5
               }
               2 -> {
                    col = 5
                    row = 6
               }
          }

          this.col = col
          boardSize = col * row
          pairs = boardSize / 2

          return arrayListOf(col, row)
     }

     fun managePairs(gl: GridLayout) {

          for (i in 0 until gl.childCount) {
               val rv = gl.getChildAt(i)
               rv.setOnClickListener { rl ->

                    if (!isBlocked) {

                         if (!gameStarted) {
                              startTimer()
                              gameStarted = true
                         }

                         if (!cards[i].isVisible) {

                              //obtenemos las partes de la tarjeta (delantera y trasera)
                              val currentCardFront = rl.findViewWithTag<CardView>("cvFront$i")
                              val currentCardBack = rl.findViewWithTag<CardView>("cvBack$i")

                              //Añadimos las animaciones de volteo
                              val currentFrontAnim = mAppContext.loadFrontAnimator.apply {
                                   setTarget(currentCardFront)
                              }
                              val currentBackAnim = mAppContext.loadBackAnimator.apply {
                                   setTarget(currentCardBack)
                              }

                              val currentAnimatorSet = AnimatorSet()
                              currentAnimatorSet.play(currentFrontAnim).with(currentBackAnim)
                              currentAnimatorSet.start()

                              //la marcamos como visible
                              cards[i].isVisible = true
                              visibleCards.add(i)

                              if (visibleCards.size == 2) {
                                   if (cards[visibleCards[0]].image == cards[visibleCards[1]].image) {
                                        //si las 2 imagenes volteadas son iguales, son pareja
                                        Log.d(TAG, "managePairs: IGUALES")
                                        gamePairs.postValue(--pairs)
                                        visibleCards.clear()
                                        if (pairs == 0) {
                                             pauseTimer()
                                             mPrefs.setWins(mPrefs.getWins() + 1)
                                             endGame.postValue(1)
                                        }
                                   } else {
                                        Log.d(TAG, "managePairs: DISTINTAS")
                                        //si son diferentes las volvemos a girar
                                        Handler(Looper.getMainLooper()).postDelayed({
                                             val rvLast = gl.getChildAt(visibleCards[0])
                                             val listCards = arrayListOf<CardView>()

                                             listCards.add(rvLast.findViewWithTag("cvFront${visibleCards[0]}"))
                                             listCards.add(rvLast.findViewWithTag("cvBack${visibleCards[0]}"))
                                             listCards.add(currentCardFront)
                                             listCards.add(currentCardBack)

                                             val alAnimators = arrayListOf<Animator>()
                                             listCards.forEachIndexed { index, cardView ->
                                                  if (index % 2 == 1) {
                                                       val frontAnimA =
                                                            mAppContext.loadFrontAnimator.apply {
                                                                 setTarget(cardView)
                                                            }
                                                       alAnimators.add(frontAnimA)
                                                  } else {
                                                       val backAnimA =
                                                            mAppContext.loadBackAnimator.apply {
                                                                 setTarget(cardView)
                                                            }
                                                       alAnimators.add(backAnimA)
                                                  }
                                             }

                                             val aSet = AnimatorSet()
                                             aSet.play(alAnimators[0])
                                                  .with(alAnimators[1]) //foto primera carta pulsada
                                             aSet.play(alAnimators[1]).with(alAnimators[2])
                                             aSet.play(alAnimators[2])
                                                  .with(alAnimators[3]) //foto segunda carta pulsada
                                             aSet.start()

                                             cards[visibleCards[0]].isVisible = false
                                             cards[visibleCards[1]].isVisible = false
                                             visibleCards.clear()
                                             isBlocked = false
                                        }, 750)
                                        isBlocked = true
                                   }
                                   gameMoves.postValue(++moves)
                              }
                         }
                    }
               }
          }
     }


     /**
      * función que devuelve un array con todas las parejas de imágenes mezcladas
      */
     private fun selectImages(allImages: ArrayList<Int>): ArrayList<Int> {
          //calculamos las parejas necesarias a partir del tamaño del tablero
          val totalPairs = boardSize / 2
          allImages.shuffle()
          allImages.subList(totalPairs - 1, allImages.lastIndex).clear()
          allImages.addAll(allImages)
          allImages.shuffle()
          return allImages
     }

     /**
      * función que devuelve las imágenes con las que se va a jugar
      */
     private fun getAllDrawables(optionTheme: Int): ArrayList<Int> {
          val drawablesFields: Array<Field> = R.drawable::class.java.fields
          val drawables = arrayListOf<Int>()

          for (field in drawablesFields) {
               try {
                    val option = when (optionTheme) {
                         0 -> "ricky_image"
                         1 -> "pokemon_image"
                         else -> "ricky_image"
                    }

                    if (field.name.contains(option)) {
                         drawables.add(field.getInt(null))
                    }
               } catch (e: Exception) {
                    e.printStackTrace()
               }
          }
          return drawables
     }


}