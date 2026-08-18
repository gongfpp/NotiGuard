package com.mass.notiguard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mass.notiguard.data.SettingsRepository
import com.mass.notiguard.domain.Notifier
import com.mass.notiguard.ui.screens.HomeScreen
import com.mass.notiguard.ui.screens.SourcesScreen
import com.mass.notiguard.ui.screens.KeywordsScreen
import com.mass.notiguard.ui.screens.HistoryScreen
import com.mass.notiguard.ui.theme.NotiGuardTheme
import com.mass.notiguard.ui.viewmodel.HomeViewModel
import com.mass.notiguard.ui.viewmodel.SourcesViewModel
import com.mass.notiguard.ui.viewmodel.KeywordsViewModel
import com.mass.notiguard.ui.viewmodel.HistoryViewModel

class MainActivity : ComponentActivity() {

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Notifier.ensureChannel(this)
        requestRuntimePermissions()
        setContent {
            NotiGuardTheme {
                AppScaffold()
            }
        }
    }

    private fun requestRuntimePermissions() {
        val needed = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) needed += Manifest.permission.READ_SMS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) needed += Manifest.permission.POST_NOTIFICATIONS
        if (needed.isNotEmpty()) requestPermission.launch(needed.toTypedArray())
    }
}

private sealed class Tab(val route: String, val label: String, val icon: ImageVector) {
    object Home : Tab("home", "首页", Icons.Filled.Home)
    object Sources : Tab("sources", "来源", Icons.Filled.Apps)
    object Keywords : Tab("keywords", "关键字", Icons.Filled.Label)
    object History : Tab("history", "记录", Icons.Filled.History)
}

private val tabs = listOf(Tab.Home, Tab.Sources, Tab.Keywords, Tab.History)

/** 返回导航图的起始目的地（用于底部导航切换时回到根） */
private fun NavGraph.findStartDestination(): NavDestination {
    var destination: NavDestination = this
    while (destination is NavGraph) {
        destination = destination.findNode(destination.startDestinationId)!!
    }
    return destination
}

@Composable
private fun AppScaffold() {
    val nav = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                val backStack by nav.currentBackStackEntryAsState()
                val current = backStack?.destination
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = current?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            nav.navigate(tab.route) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = Tab.Home.route,
            modifier = Modifier.fillMaxSize().padding(inner)
        ) {
            composable(Tab.Home.route) { HomeScreen(viewModel<HomeViewModel>()) }
            composable(Tab.Sources.route) { SourcesScreen(viewModel<SourcesViewModel>()) }
            composable(Tab.Keywords.route) { KeywordsScreen(viewModel<KeywordsViewModel>()) }
            composable(Tab.History.route) { HistoryScreen(viewModel<HistoryViewModel>()) }
        }
    }
}
