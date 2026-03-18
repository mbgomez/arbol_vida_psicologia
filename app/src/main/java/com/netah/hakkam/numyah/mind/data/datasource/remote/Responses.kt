package com.netah.hakkam.numyah.mind.data.datasource.remote

import com.netah.hakkam.numyah.mind.data.datasource.local.PostTable

data class PostResponse(
    val userId : Long?,
    val id: Long?,
    val title: String?,
    val body: String?
) {
    fun mapToTable() = PostTable(userId, id, title, body)
}