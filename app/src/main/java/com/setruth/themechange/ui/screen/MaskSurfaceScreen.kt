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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import com.setruth.themechange.components.MaskAnimModel
import com.setruth.themechange.dataStore
import com.setruth.themechange.model.ACTIVE_MASK_TAG
import com.setruth.themechange.model.MASK_CLICK_X
import com.setruth.themechange.model.MASK_CLICK_Y
import com.setruth.themechange.ui.theme.MaskAnimTheme
import kotlinx.coroutines.launch

@Composable
fun MaskSurfaceScreen() {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
                    text = "compose",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    onClick = {
                        scope.launch {
                            context.dataStore.edit {
                                it[ACTIVE_MASK_TAG] = true
                            }
                        }
                    }
                ) {
                    val tipContent = if (isDark) "ToLightTheme" else "ToDarkTheme"
                    Text(
                        text = tipContent,
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                scope.launch {
                                    context.dataStore.edit {
                                        it[MASK_CLICK_X] = coordinates.boundsInRoot().center.x
                                        it[MASK_CLICK_Y] = coordinates.boundsInRoot().center.y
                                    }
                                }
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
                            Icon(imageVector = Icons.Filled.Star, tint = MaterialTheme.colorScheme.primary, contentDescription = "FavoriteBorder")
                        }
                    }
                }
            }
        }
    }
}