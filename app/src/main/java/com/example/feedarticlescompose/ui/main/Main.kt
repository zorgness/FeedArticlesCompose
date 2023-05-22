import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.feedarticlescompose.dataclass.ArticleDto
import com.example.feedarticlescompose.R
import com.example.feedarticlescompose.ui.main.MainViewModel
import com.example.feedarticlescompose.ui.theme.FeedArticlesComposeTheme


val articlesList = listOf(
    ArticleDto(
        1,
        "test 1",
        "dsklfjqsdkljfqldksjfqlksdjfqslkj",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Grosser_Panda.JPG/1200px-Grosser_Panda.JPG",
        1,
        "20/03/23",
        1
    ),
    ArticleDto(
        2,
        "test 2",
        "dsklfjqsdkljfqldksjfqlksdjfqslkj",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Grosser_Panda.JPG/1200px-Grosser_Panda.JPG",
        1,
        "20/03/23",
        1
    ),
    ArticleDto(
        3,
        "test 3",
        "dsklfjqsdkljfqldksjfqlksdjfqslkj",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Grosser_Panda.JPG/1200px-Grosser_Panda.JPG",
        1,
        "20/03/23",
        1
    ),
    ArticleDto(
        4,
        "test 4",
        "dsklfjqsdkljfqldksjfqlksdjfqslkj",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Grosser_Panda.JPG/1200px-Grosser_Panda.JPG",
        1,
        "20/03/23",
        1
    ),
    ArticleDto(
        5,
        "test 5",
        "dsklfjqsdkljfqldksjfqlksdjfqslkj",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Grosser_Panda.JPG/1200px-Grosser_Panda.JPG",
        1,
        "20/03/23",
        1
    ),
    ArticleDto(
        6,
        "test 6",
        "dsklfjqsdkljfqldksjfqlksdjfqslkj",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Grosser_Panda.JPG/1200px-Grosser_Panda.JPG",
        1,
        "20/03/23",
        1
    ),

    ArticleDto(
        7,
        "test 5",
        "dsklfjqsdkljfqldksjfqlksdjfqslkj",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Grosser_Panda.JPG/1200px-Grosser_Panda.JPG",
        1,
        "20/03/23",
        1
    ),
    ArticleDto(
        8,
        "test 6",
        "dsklfjqsdkljfqldksjfqlksdjfqslkj",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Grosser_Panda.JPG/1200px-Grosser_Panda.JPG",
        1,
        "20/03/23",
        1
    ),
    ArticleDto(
        9,
        "test 5",
        "dsklfjqsdkljfqldksjfqlksdjfqslkj",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Grosser_Panda.JPG/1200px-Grosser_Panda.JPG",
        1,
        "20/03/23",
        1
    ),
    ArticleDto(
        10,
        "test 6",
        "dsklfjqsdkljfqldksjfqlksdjfqslkj",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Grosser_Panda.JPG/1200px-Grosser_Panda.JPG",
        1,
        "20/03/23",
        1
    ),
)



@Composable
fun MainScreen(
    navController: NavHostController?,
    viewModel: MainViewModel
) {
    val isLoading by viewModel.isLoadingStateFlow.collectAsState()

    MainContent(
        articlesList = articlesList,
        isLoading = isLoading,
        onItemClickCallback = {},
        goToNewArticle = {},
        onLogout = {}

    )
}

@Composable
fun MainContent(
    articlesList: List<ArticleDto>,
    isLoading: Boolean,
    onItemClickCallback: (ArticleDto) -> Unit,
    goToNewArticle:() -> Unit,
    onLogout:()-> Unit

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
                onLogoutIconClicked = {onLogout()}
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
                        onItemClickCallback = onItemClickCallback
                    )
                }
            }

            Footer()

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
            Icons.Rounded.Close,
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
fun Footer() {
    Spacer(
        modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
    )

    RadioBtnMainGroup()

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    )
}

@Composable
fun ItemArticle(
    item: ArticleDto,
    onItemClickCallback: (ArticleDto)->Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClickCallback.invoke(item)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Green)
                .border(1.dp, Color.Black)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.urlImage)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.feedarticles_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
                    .background(Color.White)
            )

            Spacer(modifier = Modifier
                .width(10.dp)
                .fillMaxHeight()
            )

            Text(
                text = item.titre,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
        }

    }

}

@Composable
fun RadioBtnMainGroup() {

    val categories = listOf("Tout","Sport", "Manga", "Divers")
    Row(Modifier.padding(8.dp)) {

        categories.forEach { category ->
            RadioButton(
                selected = false,
                onClick = null
            )
            Text(
                text = category,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 4.dp)

            )
        }

    }
}

