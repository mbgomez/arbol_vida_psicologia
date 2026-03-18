package com.netah.hakkam.numyah.mind.data.db.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.data.datasource.FoundationDatabase
import com.netah.hakkam.numyah.mind.data.datasource.local.PostDao
import com.netah.hakkam.numyah.mind.data.datasource.local.PostTable
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostDaoTest {
    private lateinit var postDao: PostDao
    private lateinit var db: FoundationDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().context
        db = Room.inMemoryDatabaseBuilder(context, FoundationDatabase::class.java).build()
        postDao = db.getPostDao()
    }

    @After
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun testInsertAndGetPosts() = runBlocking {
        insertPostTableList()
        val expectedPostTableList = getPostTableList()
        val actualPostTableList = postDao.getPosts()
        Assert.assertNotNull(expectedPostTableList)
        assertEquals(expectedPostTableList, actualPostTableList)
    }

    @Test
    fun testDeletePost() = runBlocking {
        insertPostTableList()
        val postTableList = getPostTableList()
        val postTable1 = postTableList[0]
        postDao.deletePosts(postTable1)
        val expectedPostTableList = postTableList.subList(1, 2)
        val actualPostTableList = postDao.getPosts()
        Assert.assertNotNull(expectedPostTableList)
        assertEquals(expectedPostTableList, actualPostTableList)
    }


    private suspend fun insertPostTableList() {
        postDao.insertPosts(getPostTableList())
    }

    private fun getPostTableList(): List<PostTable> {
        return listOf(
            PostTable(
                1L, 2L, "title1", "body1"
            ),
            PostTable(4L, 5L, "title2", "body2")
        )
    }
}
