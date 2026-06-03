package com.plcoding.bookpedia.book.presentation.book_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmp_bookpedia.composeapp.generated.resources.Res
import cmp_bookpedia.composeapp.generated.resources.favourites
import cmp_bookpedia.composeapp.generated.resources.no_favourites
import cmp_bookpedia.composeapp.generated.resources.no_search_results
import cmp_bookpedia.composeapp.generated.resources.search_results
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.presentation.book_list.components.BookList
import com.plcoding.bookpedia.book.presentation.book_list.components.BookSearchBar
import com.plcoding.bookpedia.core.presentation.DarkBlue
import com.plcoding.bookpedia.core.presentation.DesertWhite
import com.plcoding.bookpedia.core.presentation.SandYellow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookListScreenRoot(
    viewModel: BookListViewModel = koinViewModel(),
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BookListScreen(
        bookListState = state,
        onAction = { action ->
            when(action) {
                is BookListAction.OnBookClick -> onBookClick(action.book)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )

}

@Composable
private fun BookListScreen(
    bookListState: BookListState,
    onAction: (BookListAction) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val pagerState = rememberPagerState { 2 }

    val searchResultListState = rememberLazyListState()
    val favouritesListState = rememberLazyListState()

    LaunchedEffect(bookListState.searchResult) {
        searchResultListState.animateScrollToItem(0)
    }

    LaunchedEffect(bookListState.selectedTabIndex) {
        pagerState.animateScrollToPage(bookListState.selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        onAction(BookListAction.OnTabSelected(pagerState.currentPage))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BookSearchBar(
            searchQuery = bookListState.searchQuery,
            onSearchQueryChange = {
                onAction(BookListAction.OnSearchQueryChange(it))
            },
            onImeSearch = {
                keyboardController?.hide()
            },
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .padding(16.dp)
        )

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = DesertWhite
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TabRow(
                    selectedTabIndex = bookListState.selectedTabIndex,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .widthIn(max = 700.dp)
                        .fillMaxWidth(),
                    containerColor = DesertWhite,
                    contentColor = SandYellow,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            color = SandYellow,
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[bookListState.selectedTabIndex])
                        )
                    }
                ) {
                    Tab(
                        selected = bookListState.selectedTabIndex == 0,
                        onClick = {
                            onAction(BookListAction.OnTabSelected(0))
                        },
                        modifier = Modifier.weight(1f),
                        selectedContentColor = SandYellow,
                        unselectedContentColor = Color.Black.copy(alpha = 0.5f),
                        text = {
                            Text(
                                text = stringResource(Res.string.search_results),
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    )

                    Tab(
                        selected = bookListState.selectedTabIndex == 1,
                        onClick = {
                            onAction(BookListAction.OnTabSelected(1))
                        },
                        modifier = Modifier.weight(1f),
                        selectedContentColor = SandYellow,
                        unselectedContentColor = Color.Black.copy(alpha = 0.5f),
                        text = {
                            Text(
                                text = stringResource(Res.string.favourites),
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { pageIndex ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (pageIndex) {
                            0 -> {
                                if (bookListState.isLoading) {
                                    CircularProgressIndicator()
                                } else {
                                    when {
                                        bookListState.errorMessage != null -> {
                                            Text(
                                                text = bookListState.errorMessage.asString(),
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }

                                        bookListState.searchResult.isEmpty() -> {
                                            Text(
                                                text = stringResource(Res.string.no_search_results),
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }

                                        else -> {
                                            BookList(
                                                books = bookListState.searchResult,
                                                onBookClick = {
                                                    onAction(BookListAction.OnBookClick(it))
                                                },
                                                modifier = Modifier
                                                    .fillMaxSize(),
                                                scrollState = searchResultListState
                                            )
                                        }

                                    }
                                }
                            }

                            1 -> {
                                if (bookListState.favouriteBooks.isEmpty()) {
                                    Text(
                                        text = stringResource(Res.string.no_favourites),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.headlineSmall,
                                    )
                                } else {
                                    BookList(
                                        books = bookListState.favouriteBooks,
                                        onBookClick = {
                                            onAction(BookListAction.OnBookClick(it))
                                        },
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        scrollState = favouritesListState
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

val books = (1..100).map {
    Book(
        id = it.toString(),
        title = "Book $it",
        imageUrl = "https://test.com",
        authors = listOf("Kandisz Nóra"),
        description = "Description $it",
        firstPublishYear = "${1920+it}",
        averageRating = 4.67,
        languages = emptyList(),
        numPages = 100,
        numEditions = 3,
        ratingCount = 99
    )
}

@Preview
@Composable
private fun BookListScreenPreview() {
    BookListScreen(
        bookListState = BookListState(
            searchResult = books
        ),
        onAction = {}
    )
}