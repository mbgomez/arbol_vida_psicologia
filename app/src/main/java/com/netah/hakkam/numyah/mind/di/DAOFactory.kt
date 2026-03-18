package com.netah.hakkam.numyah.mind.di

import com.netah.hakkam.numyah.mind.data.datasource.FoundationDatabase
import com.netah.hakkam.numyah.mind.data.datasource.local.PostDao

object DAOProvider {
    fun providePostDao(db: FoundationDatabase): PostDao {
        return db.getPostDao()
    }
}
