package com.netah.hakkam.numyah.mind.viewmodel

import com.netah.hakkam.numyah.mind.domain.usecase.GetCachedPostsUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetPostsUseCase
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import com.netah.hakkam.numyah.mind.model.Post
import com.netah.hakkam.numyah.mind.model.Resource
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTests {
    private lateinit var postsUseCase: GetPostsUseCase
    private lateinit var cachedPostsUseCase: GetCachedPostsUseCase
    private lateinit var mainViewModel: MainViewModel

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        postsUseCase = mockk(relaxed = true)
        cachedPostsUseCase = mockk(relaxed = true)
        mainViewModel = MainViewModel(postsUseCase, cachedPostsUseCase)
    }

    @Test
    fun getPost_runPostsUseCase_Success() = coroutinesRule.runBlockingTest {
        val postList: List<Post> = listOf(Post("Title1", "Body1"))

        coEvery { postsUseCase.run() } returns flowOf(Resource.success(postList))

        mainViewModel.getPosts()
        verify(exactly = 1) { postsUseCase.run() }

        Assert.assertEquals(postList, mainViewModel.postMutableState.value)

    }

    @Test
    fun getCachedPostsUseCase_runPostsUseCase_success() = coroutinesRule.runBlockingTest {
        val postList: List<Post> = listOf(Post("Title1", "Body1"))

        coEvery { cachedPostsUseCase.run() } returns flowOf(Resource.success(postList))

        mainViewModel.getCachedPostsUseCase()
        verify(exactly = 1) { cachedPostsUseCase.run() }

        Assert.assertEquals(postList, mainViewModel.postMutableState.value)
    }

}