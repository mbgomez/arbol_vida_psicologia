package com.netah.hakkam.numyah.mind.data.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(postList: List<PostTable>)

    @Delete
    suspend fun deletePosts(post: PostTable)

    @Transaction
    @Query("SELECT * FROM posttable")
    suspend fun getPosts(): List<PostTable>

}