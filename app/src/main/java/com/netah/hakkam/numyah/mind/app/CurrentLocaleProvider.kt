package com.netah.hakkam.numyah.mind.app

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentLocaleProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun current(): Locale = context.resources.configuration.locales[0] ?: Locale.getDefault()
}
