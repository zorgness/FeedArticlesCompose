package com.example.feedarticlescompose.ui.main

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.feedarticlescompose.dataclass.ArticleDto
import com.example.feedarticlescompose.R
import com.example.feedarticlescompose.utils.Category
import com.example.feedarticlescompose.utils.Screen
import dateForrmater


@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
) {
    val articlesList by viewModel.articlesListStateFlow.collectAsState()
    val isLoading by viewModel.isLoadingStateFlow.collectAsState()
    val selectedCategory by viewModel.selectedCategoryStateflow.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(true ) {
        viewModel.goToLoginSharedFlow.collect {
            navController.navigate(it.route) {
                popUpTo(Screen.Main.route) {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(true ) {
        viewModel.goToEditSharedFlow.collect {route->
            navController.navigate(route)
        }
    }

    LaunchedEffect(true){
        viewModel.messageSharedFlow.collect { message ->
            when (message) {
                MainViewModel.MainState.ERROR_PARAM -> R.string.error_param
                MainViewModel.MainState.ERROR_SERVER -> R.string.error_server
                MainViewModel.MainState.ERROR_CONNECTION -> R.string.error_connection
                MainViewModel.MainState.ERROR_AUTHORIZATION -> R.string.error_authorization

            }.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    viewModel.fetchAllArticles()

    MainContent(
        selectedCategory = selectedCategory,
        articlesList = articlesList,
        isLoading = isLoading,
        handleItemClicked = { viewModel.updateItemClicked(it) },
        goToNewArticle = {
            navController.navigate(Screen.Creation.route)
        },
        handleLogout = { viewModel.logout() },
        handleCategory = { position->
            viewModel.updateSelectedCategory(position)
        }
    )
}

@Composable
fun MainContent(
    selectedCategory: Int,
    articlesList: List<ArticleDto>,
    isLoading: Boolean,
    handleItemClicked: (ArticleDto) -> Unit,
    goToNewArticle:() -> Unit,
    handleLogout:()-> Unit,
    handleCategory:(Int)->Unit

) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Header(
                onAddIconClicked = {goToNewArticle()},
                onLogoutIconClicked = {handleLogout()}
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ){
                items(items = articlesList){
                    ItemArticle(
                        item = it,
                        onItemClicked = handleItemClicked
                    )
                }
            }

            Footer(
                selectedCategory,
                onRadioSelected = { handleCategory(it) }
            )
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
    onAddIconClicked: () -> Unit,
    onLogoutIconClicked: () -> Unit
) {

    Box(modifier = Modifier
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
    onItemClicked: (ArticleDto)->Unit
) {

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
                        .padding(8.dp),
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.urlImage)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.feedarticles_logo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.Black, CircleShape)
                                .background(Color.White)
                )

                Text(
                    text = item.titre,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }


                Box(
                    modifier = Modifier
                                .fillMaxSize()
                                .padding(top= 12.dp)
                ) {
                    Text(
                        text = dateForrmater(item.createdAt),
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                    Text(
                        text = "Cat  ${Category.getTitle(item.categorie)}",
                        modifier = Modifier.align(Alignment.TopEnd)

                    )
                    Text(
                        text = item.descriptif,
                        modifier = Modifier
                                    .padding(top= 24.dp)
                                    .align(Alignment.CenterStart)

                    )
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

    val categories = listOf("Tout","Sport", "Manga", "Divers")
    Row(
        Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        categories.forEachIndexed { index, category ->
            RadioButton(
                selected = selectedCategory == index,
                onClick = { onRadioSelected(index) }
            )
            Text(
                text = category,
                color = Color.Black,
            )
        }
    }
}



