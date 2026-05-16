package win.liuping.photosuit_android.ui

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Editor : Screen("editor?uri={uri}") {
        fun createRoute(uri: String) = "editor?uri=${uri}"
    }
    object Presets : Screen("presets")
    object LlmChat : Screen("llm_chat")
    object Settings : Screen("settings")
    object LlmConfigList : Screen("llm_config_list")
    object LlmConfigEdit : Screen("llm_config_edit?id={id}") {
        fun createRoute(id: Long = 0L) = "llm_config_edit?id=$id"
    }
}
