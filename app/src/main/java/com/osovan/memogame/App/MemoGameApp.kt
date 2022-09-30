package com.osovan.memogame.App

import android.app.Application
import android.content.Context
import android.content.res.Resources

class MemoGameApp: Application() {
     companion object {
          lateinit var mAppContext: Context
          lateinit var mResources: Resources
     }

     override fun onCreate() {
          super.onCreate()

          mAppContext = applicationContext
          mResources = resources
     }
}