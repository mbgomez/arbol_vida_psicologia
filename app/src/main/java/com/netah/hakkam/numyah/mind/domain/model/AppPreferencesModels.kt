package com.netah.hakkam.numyah.mind.domain.model

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    fun resolve(systemInDarkTheme: Boolean): Boolean = when (this) {
        SYSTEM -> systemInDarkTheme
        LIGHT -> false
        DARK -> true
    }
}

enum class AppLanguageMode(val languageTag: String?) {
    SYSTEM(languageTag = null),
    ENGLISH(languageTag = "en"),
    SPANISH(languageTag = "es");

    companion object {
        fun fromStoredValue(value: String?): AppLanguageMode {
            return entries.firstOrNull { it.name == value } ?: SYSTEM
        }
    }
}
