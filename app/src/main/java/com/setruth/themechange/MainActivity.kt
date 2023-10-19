package com.setruth.themechange

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.setruth.themechange.components.MaskAnimModel
import com.setruth.themechange.components.MaskSurface
import com.setruth.themechange.components.MaskView
import com.setruth.themechange.model.RouteConfig
import com.setruth.themechange.model.ACTIVE_MASK_TAG
import com.setruth.themechange.model.MASK_CLICK_X
import com.setruth.themechange.model.MASK_CLICK_Y
import com.setruth.themechange.ui.screen.MaskSurfaceScreen
import com.setruth.themechange.ui.screen.MaskViewScreen
import com.setruth.themechange.ui.theme.MaskAnimTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            var isDark =isSystemInDarkTheme()
            var isDarkTheme by remember {
                mutableStateOf(isDark)
            }
            val activeMaskTag by context.dataStore.data
                .map { preferences ->
                    preferences[ACTIVE_MASK_TAG] ?: false
                }
                .collectAsState(initial = false)
            val maskClickX by context.dataStore.data
                .map { preferences ->
                    preferences[MASK_CLICK_X] ?: 0f
                }
                .collectAsState(initial = 0f)
            val maskClickY by context.dataStore.data
                .map { preferences ->
                    preferences[MASK_CLICK_Y] ?: 0f
                }
                .collectAsState(initial = 0f)
            MaskAnimTheme(isDarkTheme) {
                MaskSurface(
                    maskComplete = {
                        scope.launch {
                            context.dataStore.edit {
                                it[ACTIVE_MASK_TAG] = false
                            }
//                            isDarkTheme = !isDarkTheme
                        }
                    },
                    animTime = tween(700)
                ) { maskActiveEvent ->
                    LaunchedEffect(activeMaskTag) {
                        if (!activeMaskTag) return@LaunchedEffect
                        Log.e("TAG", "onCreate:$isDark ", )
//                        if (isDarkTheme)
//                            maskActiveEvent(MaskAnimModel.SHRINK, maskClickX, maskClickY)
//                        else
                            maskActiveEvent(MaskAnimModel.EXPEND, maskClickX, maskClickY)
                    }
                    MainView()
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView() {
    var activeIndex by remember {
        mutableStateOf(0)
    }
    val navInfoList = remember {
        mutableStateListOf(
            Pair("compose", RouteConfig.COMPOSE),
            Pair("xml", RouteConfig.CUSTOM_VIEW)
        )
    }
    val appController = rememberNavController()
    appController.addOnDestinationChangedListener { _, destination, _ ->
        for ((index, item) in navInfoList.withIndex()) {
            if (item.second == destination.route) {
                activeIndex = index
                return@addOnDestinationChangedListener
            }
        }
    }
    Scaffold(bottomBar = {
        NavigationBar {
            navInfoList.forEachIndexed { index, info ->
                NavigationBarItem(
                    icon = {
                        Text(text = info.first)
                    },
                    onClick = {
                        activeIndex = index
                        appController.navigateSingleTopTo(info.second)
                    },
                    selected = activeIndex == index
                )
            }
        }
    }) {
        val padding = it
        NavHost(navController = appController, startDestination = RouteConfig.COMPOSE) {
            composable(RouteConfig.COMPOSE) {
                MaskSurfaceScreen()
            }
            composable(RouteConfig.CUSTOM_VIEW) {
                MaskViewScreen()
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
