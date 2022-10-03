package com.osovan.memogame.data

import android.content.Context
import com.osovan.memogame.App.MemoGameApp.Companion.mResources
import com.osovan.memogame.R

class Preferences(context: Context) {
     private val userPrefs =
          context.getSharedPreferences(context.getString(R.string.pref_key), Context.MODE_PRIVATE)!!

     fun getWins(): Int {
          return userPrefs.getInt(mResources.getString(R.string.pref_wins), 0)
     }

     fun setWins(wins: Int) {
          userPrefs
               .edit()
               .putInt(mResources.getString(R.string.pref_wins), wins)
               .apply()
     }
}