package com.setruth.themechange.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import com.setruth.themechange.components.activeMaskView
import com.setruth.themechange.dataStore
import com.setruth.themechange.model.IS_DARK_MODEL
import com.setruth.themechange.model.MaskAnimModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun MaskViewScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var buttonActive by remember {
        mutableStateOf(true)
    }
    var clickX by remember {
        mutableStateOf(0f)
    }
    var clickY by remember {
        mutableStateOf(0f)
    }
    val isDarkTheme by context.dataStore.data
        .map { preferences ->
            preferences[IS_DARK_MODEL] ?: false
        }
        .collectAsState(initial = false)
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
                    enabled = buttonActive,
                    onClick = {
                        buttonActive=false
                        context.activeMaskView(
                            animModel = if (isDarkTheme) MaskAnimModel.SHRINK else MaskAnimModel.EXPEND,
                            clickX = clickX,
                            clickY = clickY,
                            animTime = 700,
                            maskComplete = {
                                scope.launch {
                                    context.dataStore.edit {
                                        it[IS_DARK_MODEL] = !isDarkTheme
                                    }
                                }
                            },
                            maskAnimFinish = {
                                buttonActive=true
                            },
                        )
                    }
                ) {
                    val tipContent = if (isDarkTheme) "ToLightTheme" else "ToDarkTheme"
                    Text(
                        text = tipContent,
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                clickX = coordinates.boundsInWindow().center.x
                                clickY = coordinates.boundsInWindow().center.y
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
                                text = it.toString(),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "Favorite"
                            )
                        }
                    }
                }
            }
        }
    }
}