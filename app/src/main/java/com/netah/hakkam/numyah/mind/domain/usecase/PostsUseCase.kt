package com.netah.hakkam.numyah.mind.domain.usecase


import com.netah.hakkam.numyah.mind.data.repository.IPostRepository
import com.netah.hakkam.numyah.mind.model.Post
import com.netah.hakkam.numyah.mind.model.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val postRepository: IPostRepository
) : FlowInteractorNoParams<Resource<List<Post>>>() {
    override fun buildUseCase(): Flow<Resource<List<Post>>> {
        return postRepository.getPosts()
    }
}

class GetCachedPostsUseCase @Inject constructor(
    private val postRepository: IPostRepository
) : FlowInteractorNoParams<Resource<List<Post>>>() {
    override fun buildUseCase(): Flow<Resource<List<Post>>> {
        return postRepository.getCachedPosts()
    }
}