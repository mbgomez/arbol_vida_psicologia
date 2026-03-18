package com.netah.hakkam.numyah.mind.mapper

import com.netah.hakkam.numyah.mind.data.datasource.local.PostTable
import com.netah.hakkam.numyah.mind.model.Post

class PostMapper : MapperData<PostTable, Post> {
    override fun fromDataToDomain(table: PostTable): Post = table.run { Post(title, body) }

}