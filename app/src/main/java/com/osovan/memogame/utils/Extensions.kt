package com.osovan.memogame.utils

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import com.osovan.memogame.R

val Any.TAG: String
     get() {
          val tag = javaClass.simpleName
          return if (tag.length <= 23) tag else tag.substring(0, 23)
     }

val Context.loadFrontAnimator: Animator
     get() {
          return AnimatorInflater.loadAnimator(
               this,
               R.animator.animator_front
          )
     }

val Context.loadBackAnimator: Animator
     get() {
          return AnimatorInflater.loadAnimator(
               this,
               R.animator.animator_back
          )
     }