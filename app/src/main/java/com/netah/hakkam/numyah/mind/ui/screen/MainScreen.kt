package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.model.Post
import com.netah.hakkam.numyah.mind.ui.components.CardView
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.MainViewModel


@SuppressWarnings("unused")
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navigateToDetails: ((index: Int) -> Unit)? = null
) {

    viewModel.getPosts()

    MainContent(
        postListState = viewModel.postMutableState,
        isLoadingState = viewModel.isLoadingMutableState,
        navigateToDetails = navigateToDetails
    )
}

@Composable
fun MainContent(
    postListState: State<List<Post>>,
    navigateToDetails: ((index: Int) -> Unit)?,
    isLoadingState: State<Boolean>
) {
    val smallPadding = dimensionResource(R.dimen.padding_small)
    val isLoading by rememberSaveable { isLoadingState }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = smallPadding)
    ) {
        if (isLoading) {
            LoadingView()
        } else {
            PostListView(postListState = postListState, navigateToDetails = navigateToDetails)
        }
    }
}


@Composable
fun PostListView(
    postListState: State<List<Post>>,
    navigateToDetails: ((index: Int) -> Unit)? = null
) {
    val postList by rememberSaveable { postListState }
    val smallPadding = dimensionResource(R.dimen.padding_small)
    val smallHeight = dimensionResource(R.dimen.height_small)

    LazyColumn {
        itemsIndexed(postList) { index, post ->
            CardView(
                modifier = Modifier
                    .padding(top = smallPadding, start = smallPadding, end = smallPadding),
                onClick = {
                    navigateToDetails?.invoke(index)
                }
            ) {

                Column {
                    Text(
                        text = post.title ?: "",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(smallHeight))

                    Text(
                        text = post.body ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )

                }
            }
        }
    }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val description = stringResource(R.string.progress_indicator_desccription)
        CircularProgressIndicator(modifier = Modifier.semantics {
            this.contentDescription = description
        }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultLoadingPreview() {
    val postListState: State<List<Post>> = remember { mutableStateOf(emptyList()) }
    val isLoadingState: State<Boolean> = remember { mutableStateOf(true) }
    AppTheme {
        MainContent(
            postListState = postListState,
            navigateToDetails = null,
            isLoadingState = isLoadingState
        )
    }
}