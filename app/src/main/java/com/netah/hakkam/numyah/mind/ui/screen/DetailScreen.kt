package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.model.Post
import com.netah.hakkam.numyah.mind.ui.components.CardView
import com.netah.hakkam.numyah.mind.viewmodel.MainViewModel

@SuppressWarnings("unused")
@Composable
fun DetailScreen(viewModel: MainViewModel, index: Int) {

    viewModel.getCachedPostsUseCase()

    DetailContent(
        position = index,
        postListState = viewModel.postMutableState,
        isLoadingState = viewModel.isLoadingMutableState,
    )
}

@Composable
fun DetailContent(
    position: Int,
    postListState: MutableState<List<Post>>,
    isLoadingState: MutableState<Boolean>
) {
    val isLoading by rememberSaveable { isLoadingState }
    val smallPadding = dimensionResource(R.dimen.padding_small)
    Box(
        modifier = Modifier
            .padding(smallPadding)
            .fillMaxWidth()
            .heightIn(min = 80.dp, max = 240.dp)
            .clipToBounds()
    ) {
        if (isLoading) {
            LoadingView()
        } else {
            DetailView(position = position, postListState = postListState)
        }
    }
}


@Composable
fun DetailView(
    position: Int,
    postListState: MutableState<List<Post>>
) {
    val postList by rememberSaveable { postListState }
    val post = postList[position]
    Column {
        CardView(
        ) {
            Column {
                Text(
                    text = post.title ?: "",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = post.body ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )

            }
        }
    }
}
