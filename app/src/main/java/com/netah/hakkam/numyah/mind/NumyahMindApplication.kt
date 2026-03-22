package com.netah.hakkam.numyah.mind

import android.app.Application
import com.netah.hakkam.numyah.mind.app.AppLanguageManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber
import timber.log.Timber.Forest.plant

@HiltAndroidApp
class NumyahMindApplication : Application() {

    @Inject
    lateinit var appLanguageManager: AppLanguageManager

    override fun onCreate() {
        super.onCreate()
        appLanguageManager.applyStoredLanguageMode()
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
    }
}
