package me.qingshu.latin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

lateinit var application: Application private set

@HiltAndroidApp
class App: Application(){
    override fun onCreate() {
        super.onCreate()
        application = this
    }
}