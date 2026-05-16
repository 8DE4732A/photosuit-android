package win.liuping.photosuit_android.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import win.liuping.photosuit_android.ui.screen.editor.EditorScreen
import win.liuping.photosuit_android.ui.screen.home.HomeScreen
import win.liuping.photosuit_android.ui.screen.llm.LlmChatScreen
import win.liuping.photosuit_android.ui.screen.llm.LlmConfigEditScreen
import win.liuping.photosuit_android.ui.screen.llm.LlmConfigListScreen
import win.liuping.photosuit_android.ui.screen.presets.PresetsScreen
import win.liuping.photosuit_android.ui.screen.settings.SettingsScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onPickPhoto = { uri ->
                    navController.navigate(Screen.Editor.createRoute(Uri.encode(uri.toString())))
                },
                onOpenPresets = { navController.navigate(Screen.Presets.route) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenLlmChat = { navController.navigate(Screen.LlmChat.route) },
            )
        }
        composable(
            route = Screen.Editor.route,
            arguments = listOf(navArgument("uri") { type = NavType.StringType; nullable = true }),
        ) { backStack ->
            val uriStr = backStack.arguments?.getString("uri")
            val uri = uriStr?.let { Uri.decode(it) }?.let { Uri.parse(it) }
            EditorScreen(
                photoUri = uri,
                onBack = { navController.popBackStack() },
                onOpenPresets = { navController.navigate(Screen.Presets.route) },
                onOpenLlmChat = { navController.navigate(Screen.LlmChat.route) },
            )
        }
        composable(Screen.Presets.route) {
            PresetsScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.LlmChat.route) {
            LlmChatScreen(
                onBack = { navController.popBackStack() },
                onOpenLlmConfigs = { navController.navigate(Screen.LlmConfigList.route) },
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenLlmConfigs = { navController.navigate(Screen.LlmConfigList.route) },
            )
        }
        composable(Screen.LlmConfigList.route) {
            LlmConfigListScreen(
                onBack = { navController.popBackStack() },
                onAddConfig = { navController.navigate(Screen.LlmConfigEdit.createRoute()) },
                onEditConfig = { id -> navController.navigate(Screen.LlmConfigEdit.createRoute(id)) },
            )
        }
        composable(
            route = Screen.LlmConfigEdit.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = 0L }),
        ) { backStack ->
            val id = backStack.arguments?.getLong("id") ?: 0L
            LlmConfigEditScreen(
                configId = id,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
