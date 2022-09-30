package com.osovan.memogame.ui.game

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
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
import com.osovan.memogame.App.MemoGameApp.Companion.mResources
import com.osovan.memogame.R
import com.osovan.memogame.data.Card
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
     var cards = mutableListOf<Card>()

     private var col by Delegates.notNull<Int>()
     private var boardSize by Delegates.notNull<Int>()
     private var pairs = 0


     fun onCreate(gameMode: Int, gameTheme: Int) {
          viewModelScope.launch {
               withContext(Dispatchers.IO) {
                    gameBoardSize.postValue((setTableSize(gameMode)))
                    gamePairs.postValue(pairs)
                    gameBoardViews.postValue(createBoardViews())
                    cardImages.postValue(selectImages(getAllDrawables(gameTheme)))

               }
          }
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