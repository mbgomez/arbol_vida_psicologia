package com.netah.hakkam.numyah.mind.data.repository

import com.netah.hakkam.numyah.mind.data.datasource.local.PostDao
import com.netah.hakkam.numyah.mind.data.datasource.local.PostTable
import com.netah.hakkam.numyah.mind.data.datasource.remote.InternetConnectionChecker
import com.netah.hakkam.numyah.mind.data.datasource.remote.PostService
import com.netah.hakkam.numyah.mind.extensions.otherwise
import com.netah.hakkam.numyah.mind.extensions.parse
import com.netah.hakkam.numyah.mind.mapper.PostMapper
import com.netah.hakkam.numyah.mind.model.Post
import com.netah.hakkam.numyah.mind.model.Resource
import com.netah.hakkam.numyah.mind.model.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


interface IPostRepository {
    fun getPosts(): Flow<Resource<List<Post>>>
    fun getCachedPosts(): Flow<Resource<List<Post>>>
}

class PostRepository @Inject constructor(
    private val postService: PostService,
    private val internetConnectionChecker: InternetConnectionChecker,
    private val postDao: PostDao,
    private val postMapper: PostMapper
) : IPostRepository {

    fun getPostsInternet(): Flow<Resource<List<PostTable>>> =
        flow {
            val response = postService.getPosts()
            val responseParsed = response.parse()
            val resource = if (responseParsed.status == Status.SUCCESS) {
                val postTable = responseParsed.data?.map { it.mapToTable() }
                postTable?.let { table ->
                    postDao.insertPosts(table)
                    Resource.success(table)
                }.otherwise {
                    Resource.success(emptyList())
                }

            } else {
                Resource.error(responseParsed.errorCode)
            }

            emit(resource)
        }

    fun getPostsDb(): Flow<Resource<List<PostTable>>> =
        flow {
            emit(Resource.success(postDao.getPosts()))
        }

    override fun getCachedPosts(): Flow<Resource<List<Post>>> = getPostsDb().map { mapPosts(it) }

    override fun getPosts(): Flow<Resource<List<Post>>> =
        if (internetConnectionChecker.isOnline()) {
            getPostsInternet()
        } else {
            getPostsDb()
        }.map {
            mapPosts(it)
        }

    fun mapPosts(resource: Resource<List<PostTable>>) =
        if (resource.status == Status.SUCCESS) {
            val t = resource.data?.map { postData ->
                postMapper.fromDataToDomain(postData)
            } ?: emptyList()
            Resource.success(t)
        } else {
            Resource.error(resource.errorCode)
        }


}