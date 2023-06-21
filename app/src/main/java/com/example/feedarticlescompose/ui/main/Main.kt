package com.example.feedarticlescompose.ui.main

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.feedarticlescompose.dataclass.ArticleDto
import com.example.feedarticlescompose.R
import com.example.feedarticlescompose.ui.darkmode.DarkViewModel
import com.example.feedarticlescompose.ui.main.MainViewModel.DeleteState.*
import com.example.feedarticlescompose.ui.theme.BlueApp
import com.example.feedarticlescompose.utils.Category
import com.example.feedarticlescompose.utils.Screen
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dateForrmater

@ExperimentalUnitApi
@ExperimentalMaterialApi
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    darkViewModel: DarkViewModel,
) {
    val articlesList by viewModel.articlesToShowStateFlow.collectAsState()
    val isLoading by viewModel.isLoadingStateFlow.collectAsState()
    val selectedCategory by viewModel.selectedCategoryStateflow.collectAsState()
    val isExpandedId by viewModel.expandedIdStateFlow.collectAsState()
    val isRefreshing by viewModel.isRefreshingStateFlow.collectAsState()
    val currentUserId by viewModel.currentUserIdStateflow.collectAsState()
    val isDarkMode by darkViewModel.isDarkMode.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(true) {
        viewModel.goToLoginSharedFlow.collect {
            navController.navigate(it.route) {
                popUpTo(Screen.Main.route) {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.goToEditSharedFlow.collect { route ->
            navController.navigate(route)
        }
    }

    LaunchedEffect(true) {
        viewModel.mainStateSharedFlow.collect { state ->
            when(state) {
                MainViewModel.MainState.ERROR_PARAM -> R.string.error_param
                MainViewModel.MainState.ERROR_SERVER -> R.string.error_server
                MainViewModel.MainState.ERROR_CONNECTION -> R.string.error_connection
                MainViewModel.MainState.ERROR_AUTHORIZATION -> R.string.error_authorization
                MainViewModel.MainState.ERROR_SERVICE -> R.string.error_service
            }.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.deleteStateSharedFlow.collect { state ->
            when (state) {
                SUCCESS -> R.string.delete_success
                FAILURE -> R.string.delete_failure
                ERROR_SERVICE -> R.string.error_service
                ERROR_SERVER -> R.string.error_server
                ERROR_CONNECTION -> R.string.error_connection
                ERROR_AUTHORIZATION -> R.string.error_authorization
                ERROR_PARAM -> R.string.error_param
            }.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    MainContent(
        selectedCategory = selectedCategory,
        articlesList = articlesList,
        currentUserId = currentUserId,
        isLoading = isLoading,
        isExpandedId = isExpandedId,
        isRefreshing = isRefreshing,
        isDarkMode = isDarkMode,
        handleItemClicked = { viewModel.handleItemClicked(it) },
        handleExpandReset = { viewModel.resetExpandedId() },
        goToNewArticle = {
            navController.navigate(Screen.Creation.route)
        },
        handleDarkMode = { darkViewModel.setDarkMode() },
        handleLogout = { viewModel.logout() },
        handleCategory = { position ->
            viewModel.updateSelectedCategory(position)
        },
        handleRefresh = { viewModel.refresh() },
        handleDelete = { viewModel.deleteArticle(it) }
    )
}

@ExperimentalUnitApi
@ExperimentalMaterialApi
@Composable
fun MainContent(
    selectedCategory: Int,
    articlesList: List<ArticleDto>,
    currentUserId: Long,
    isLoading: Boolean,
    isRefreshing: Boolean,
    isExpandedId: Long,
    isDarkMode: Boolean,
    handleItemClicked: (ArticleDto) -> Unit,
    handleExpandReset: () -> Unit,
    goToNewArticle: () -> Unit,
    handleDarkMode: () -> Unit,
    handleLogout: () -> Unit,
    handleCategory: (Int) -> Unit,
    handleRefresh: () -> Unit,
    handleDelete: (Long) -> Unit,
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { handleRefresh() },
            indicator = { _ ,trigger->
                SwipeRefreshIndicator(
                    state = rememberSwipeRefreshState(isRefreshing),
                    refreshTriggerDistance = trigger,
                    contentColor = BlueApp,
                    modifier =  Modifier.padding(top= if(isRefreshing)120.dp else 0.dp) ,
                    elevation = 4.dp
                )
            }
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Header(
                    isDarkMode = isDarkMode,
                    onAddIconClicked = { goToNewArticle() },
                    onLogoutIconClicked = { handleLogout() },
                    onSwitchDarkMode = { handleDarkMode() }
                )


                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    items(
                        items = articlesList,
                      /*
                       *  rememberDismissState will work according to the item id
                       *  instead of the list position.
                       */
                        key = {item -> item.id}
                    ) {item->

                       /*
                        *  IF CURRENT USER IS CREATOR
                        */
                        if(item.idU == currentUserId) {

                            val dismissState = rememberDismissState()

                            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                                handleDelete(item.id)
                            }

                            SwipeToDismiss(
                                state = dismissState,
                                directions = setOf(
                                    DismissDirection.EndToStart
                                ),
                                dismissThresholds = { direction ->
                                    FractionalThreshold(
                                        // action is triggered when item is swiped at 90%
                                        if (direction == DismissDirection.EndToStart) 0.1f else 0.05f
                                    )
                                },
                                background = {

                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .background(Color.Red),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            Icons.Outlined.Delete,
                                            contentDescription = null,
                                        )
                                    }
                                },
                                dismissContent = {
                                    ItemArticle(
                                        item = item,
                                        isExpandedId,
                                        onItemClicked = handleItemClicked,
                                        onExpandResetClicked = handleExpandReset
                                    )
                                }
                            )

                            Divider(Modifier.fillMaxWidth(), Color.DarkGray)

                        } else {

                            ItemArticle(
                                item = item,
                                isExpandedId,
                                onItemClicked = handleItemClicked,
                                onExpandResetClicked = handleExpandReset
                            )
                        }
                    }
                }

                Footer(
                    selectedCategory,
                    onRadioSelected = { handleCategory(it) }
                )
            }
        }

        AnimatedVisibility(
            visible = isLoading,
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            CircularProgressIndicator()
        }

    }
}


@Composable
fun Header(
    isDarkMode: Boolean,
    onAddIconClicked: () -> Unit,
    onLogoutIconClicked: () -> Unit,
    onSwitchDarkMode: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Icon(
            Icons.Outlined.Add,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.CenterStart)
                .padding(8.dp)
                .clickable { onAddIconClicked() }
        )

        Switch(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end= 80.dp),
            checked = isDarkMode,
            onCheckedChange = {
                onSwitchDarkMode()
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.DarkGray
            )
        )
        Icon(
            Icons.Outlined.ExitToApp,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.CenterEnd)
                .padding(8.dp)
                .clickable { onLogoutIconClicked() }
        )
    }
}


@Composable
fun ItemArticle(
    item: ArticleDto,
    isExpandedId: Long,
    onItemClicked: (ArticleDto) -> Unit,
    onExpandResetClicked: () -> Unit
) {


    val animatedSize by animateDpAsState(
        targetValue = if(isExpandedId == item.id) 90.dp else 60.dp,
        animationSpec = tween(1000, 100, easing = LinearOutSlowInEasing)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClicked(item)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Category.getColor(item.categorie))
                .border(1.dp, Color.Black)
                .padding(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {


                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.urlImage)
                        .crossfade(true)
                        .build(),
                    error = painterResource(R.drawable.feedarticles_logo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(animatedSize)
                        .clip(CircleShape)
                        .border(1.dp, Color.Black, CircleShape)
                        .background(Color.White)
                )

                Text(
                    text = item.titre,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 8.dp)

                )
            if(item.id == isExpandedId) {
                Spacer(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                Icon(
                    Icons.Default.ExpandLess,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        onExpandResetClicked()
                    }
                )
            }

            }

            AnimatedVisibility(
                visible = item.id == isExpandedId,
                enter = expandVertically(),
                exit =  slideOutVertically() + shrinkVertically() + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = "Du ${dateForrmater(item.createdAt)}",
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                    Text(
                        text = "Cat ${Category.getTitle(item.categorie)}",
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.TopEnd)

                    )
                    Text(
                        text = item.descriptif,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .align(Alignment.CenterStart)

                    )
                }

            }

        }
    }
}

@Composable
fun Footer(selectedCategory: Int, onRadioSelected: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        RadioBtnMainGroup(
            selectedCategory,
            onRadioSelected = { onRadioSelected(it) }
        )
    }
}

@Composable
fun RadioBtnMainGroup(
    selectedCategory: Int,
    onRadioSelected: (Int) -> Unit
) {

    val categories = listOf("Tout", "Sport", "Manga", "Divers")
    Row(
        Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        categories.forEachIndexed { index, category ->
            RadioButton(
                selected = selectedCategory == index,
                onClick = { onRadioSelected(index) },
                colors = RadioButtonDefaults.colors(BlueApp)
            )
            Text(
                text = category,
                //color = Color.Black,
            )
        }
    }
}



