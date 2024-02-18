package com.example.codriving.Homepage.ui.home


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.codriving.Homepage.domain.SearchViewModel
import com.example.codriving.Homepage.ui.home.navigationBar.navigationBar
import com.example.codriving.R
import com.example.codriving.navigation.AppScreens


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController) {
    val viewModel = SearchViewModel()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { HeaderHome(scrollBehavior) },
        bottomBar = { navigationBar() },
        content = { paddingValues ->
            BodyHome(paddingValues, viewModel,navController) // Pass paddingValues to BodyHome if needed
        }

    )
}


@Composable
fun SearchBar(viewModel: SearchViewModel, navController: NavController) {

    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)

    ) {
        TextField(
            value = searchText,
            onValueChange = { viewModel.onSearchTextChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .background(Color.LightGray)
                .clip(RoundedCornerShape(10.dp)),

            label = { Text("Search") },
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource){
                    interactionSource.interactions.collect{
                        if(it is PressInteraction.Release){
                            navController.navigate(route = AppScreens.SearchScreen.route)
                        }
                    }
                }
            }
        )
        if (isSearching) {
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderHome(scrollBehavior: TopAppBarScrollBehavior) {

    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile"
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}


@Composable
fun BodyHome(paddingValues: PaddingValues, viewModel: SearchViewModel, navController: NavController) {
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        Column {
            SearchBar(viewModel,navController)
            Text(
                text = "Categories",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold, // Aplica negrita al texto
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Start

            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {

                item {
                    Column {
                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                        ) {
                            AsyncImage(
                                model = "https://loremflickr.com/520/440/SUV",
                                contentDescription = null,
                                modifier = Modifier.clip(RoundedCornerShape(10.dp))
                            )
                        }
                        Text(text = "SUV")

                    }
                }
                item {
                    Column {
                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                        ) {
                            AsyncImage(
                                model = "https://loremflickr.com/520/440/Minivan",
                                contentDescription = null,
                                modifier = Modifier.clip(RoundedCornerShape(10.dp))

                            )
                        }
                        Text(text = "Minivan")
                    }


                }
                item {
                    Column {
                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                        ) {
                            AsyncImage(
                                model = "https://loremflickr.com/520/440/Truck",
                                contentDescription = null,
                                modifier = Modifier.clip(RoundedCornerShape(10.dp))

                            )
                        }
                        Text(text = "Truck")

                    }

                }
                item {
                    Column {
                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                        ) {
                            AsyncImage(
                                model = "https://loremflickr.com/520/440/Luxury",
                                contentDescription = null,
                                modifier = Modifier.clip(RoundedCornerShape(10.dp))

                            )
                        }
                        Text(text = "Luxury")

                    }
                }
                item {
                    Column {
                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                        ) {
                            AsyncImage(
                                model = "https://loremflickr.com/520/440/Convertible",
                                contentDescription = null,
                                modifier = Modifier.clip(RoundedCornerShape(10.dp))

                            )
                        }
                        Text(text = "Convertible")

                    }
                }
            }
        }
        Text(
            text = "Featured Deals",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold, // Aplica negrita al texto
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Start
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            item {
                Column {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                    ) {
                        AsyncImage(
                            model = "https://loremflickr.com/520/440/SUV",
                            contentDescription = null,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp))
                        )
                    }
                    Text(text = "SUV")

                }
            }
            item {
                Column {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                    ) {
                        AsyncImage(
                            model = "https://loremflickr.com/520/440/Minivan",
                            contentDescription = null,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp))

                        )
                    }
                    Text(text = "Minivan")
                }


            }
            item {
                Column {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                    ) {
                        AsyncImage(
                            model = "https://loremflickr.com/520/440/Truck",
                            contentDescription = null,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp))

                        )
                    }
                    Text(text = "Truck")

                }

            }
            item {
                Column {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                    ) {
                        AsyncImage(
                            model = "https://loremflickr.com/520/440/Luxury",
                            contentDescription = null,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp))

                        )
                    }
                    Text(text = "Luxury")

                }
            }
            item {
                Column {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                    ) {
                        AsyncImage(
                            model = "https://loremflickr.com/520/440/Convertible",
                            contentDescription = null,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp))

                        )
                    }
                    Text(text = "Convertible")

                }
            }

        }
        Text(
            text = "Recent Searches",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold, // Aplica negrita al texto
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Start
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            item {
                Text(text = "Hola")
            }
        }
    }
}

@Composable
fun BottonBar() {

}
