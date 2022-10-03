package com.osovan.memogame.App

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.osovan.memogame.data.Preferences


class MemoGameApp: Application() {
     companion object {
          lateinit var mAppContext: Context
          lateinit var mResources: Resources
          lateinit var  mPrefs: Preferences
     }

     override fun onCreate() {
          super.onCreate()

          mAppContext = applicationContext
          mResources = resources
          mPrefs = Preferences(applicationContext)
     }
}