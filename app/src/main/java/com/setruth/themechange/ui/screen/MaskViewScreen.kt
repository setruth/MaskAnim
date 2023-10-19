package com.setruth.themechange.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import com.setruth.themechange.dataStore
import com.setruth.themechange.model.ACTIVE_MASK_TAG
import com.setruth.themechange.model.MASK_CLICK_X
import com.setruth.themechange.model.MASK_CLICK_Y
import kotlinx.coroutines.launch

@Composable
fun MaskViewScreen() {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "customView",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    onClick = {

                    }
                ) {
                    val tipContent = if (isDark) "ToLightTheme" else "ToDarkTheme"
                    Text(
                        text = tipContent,
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                coordinates.boundsInRoot().center.x
                                coordinates.boundsInRoot().center.y
                            },
                    )
                }
            }
            LazyColumn {
                items(50) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = it.toString(),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(imageVector = Icons.Filled.Favorite, tint = MaterialTheme.colorScheme.error, contentDescription = "Favorite")
                        }
                    }
                }
            }
        }
    }
}