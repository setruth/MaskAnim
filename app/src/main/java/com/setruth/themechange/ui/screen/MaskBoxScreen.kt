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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.setruth.themechange.model.DARK_SWITCH_ACTIVE
import com.setruth.themechange.model.MASK_CLICK_X
import com.setruth.themechange.model.MASK_CLICK_Y
import com.setruth.themechange.model.THEME_SWITCH_ACTIVE
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun MaskSurfaceScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var themeSwitchPositionX by remember {
        mutableStateOf(0f)
    }
    var themeSwitchPositionY by remember {
        mutableStateOf(0f)
    }
    var darkSwitchPositionX by remember {
        mutableStateOf(0f)
    }
    var darkSwitchPositionY by remember {
        mutableStateOf(0f)
    }
    val darkSwitchActive by context.dataStore.data.map { preferences ->
            preferences[DARK_SWITCH_ACTIVE] ?: false
        }.collectAsState(initial = false)
    val themeSwitchActive by context.dataStore.data.map { preferences ->
            preferences[THEME_SWITCH_ACTIVE] ?: false
        }.collectAsState(initial = false)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
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

                Button(modifier = Modifier.padding(end = 10.dp),
                    enabled = !themeSwitchActive,
                    onClick = {
                        scope.launch {
                            context.dataStore.edit {
                                it[MASK_CLICK_X] = themeSwitchPositionX
                                it[MASK_CLICK_Y] = themeSwitchPositionY
                                it[THEME_SWITCH_ACTIVE] = true
                            }
                        }
                    }) {
                    val tipContent = "SwitchTheme"
                    Text(
                        text = tipContent,
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                                themeSwitchPositionX = coordinates.boundsInRoot().center.x
                                themeSwitchPositionY = coordinates.boundsInRoot().center.y
                            },
                    )
                }
                Button(enabled = !darkSwitchActive && !themeSwitchActive, onClick = {
                    scope.launch {
                        context.dataStore.edit {
                            it[MASK_CLICK_X] = darkSwitchPositionX
                            it[MASK_CLICK_Y] = darkSwitchPositionY
                            it[DARK_SWITCH_ACTIVE] = true
                        }
                    }
                }) {
                    val tipContent = if (isSystemInDarkTheme()) "LightTheme" else "DarkTheme"
                    Text(
                        text = tipContent,
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                                darkSwitchPositionX = coordinates.boundsInRoot().center.x
                                darkSwitchPositionY = coordinates.boundsInRoot().center.y
                            },
                    )
                }
            }

            LazyColumn {
                items(50) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = it.toString(), fontSize = 30.sp, fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Filled.Star,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "FavoriteBorder"
                            )
                        }
                    }
                }
            }
        }
    }
}