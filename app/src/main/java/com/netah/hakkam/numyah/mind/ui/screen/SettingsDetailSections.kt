package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.netah.hakkam.numyah.mind.ui.components.AppFooterCard
import com.netah.hakkam.numyah.mind.ui.components.AppHeroCard
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.AppSectionCard

@Composable
fun SettingsDetailScaffold(
    paddingValues: PaddingValues,
    content: @Composable ColumnScope.() -> Unit
) {
    AppScreenColumn(paddingValues = paddingValues, content = content)
}

@Composable
fun SettingsDetailHero(
    eyebrow: String,
    title: String,
    body: String
) {
    AppHeroCard(eyebrow = eyebrow, title = title, body = body)
}

@Composable
fun SettingsDetailFooter(
    text: String
) {
    AppFooterCard(text = text)
}

@Composable
fun SettingsDetailSection(
    title: String,
    body: String
) {
    AppSectionCard(title = title, body = body)
}
