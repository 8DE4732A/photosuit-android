package win.liuping.photosuit_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import win.liuping.photosuit_android.ui.AppNavHost
import win.liuping.photosuit_android.ui.theme.PhotoSuitTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoSuitTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}
