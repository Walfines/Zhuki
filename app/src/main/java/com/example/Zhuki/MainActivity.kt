package com.example.Zhuki

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainTabsScreen()
                }
            }
        }
    }
}

@Composable
fun MainTabsScreen() {
    val tabs = listOf("Регистрация", "Игра", "Правила", "Авторы", "Настройки")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("game_settings", Context.MODE_PRIVATE)
    }

    // Настройки игры читаем один раз при старте
    val speed = prefs.getFloat("speed", 1f)
    val maxRoaches = prefs.getFloat("maxRoaches", 10f).toInt()
    val roundDuration = prefs.getFloat("roundDuration", 60f).toInt()

    // null — играем; не null — показываем GameOver
    var gameStats by remember { mutableStateOf<GameStats?>(null) }
    // ключ для перезапуска GameScreen
    var gameKey by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when (page) {
                0 -> RegistrationScreen()
                1 -> {
                    val stats = gameStats
                    if (stats == null) {
                        key(gameKey) {
                            GameScreen(
                                speedMultiplier = speed,
                                maxRoaches = maxRoaches,
                                roundDurationSec = roundDuration,
                                onGameOver = { gameStats = it }
                            )
                        }
                    } else {
                        GameOverScreen(
                            stats = stats,
                            onRestart = {
                                gameStats = null
                                gameKey++          // пересоздаёт GameScreen
                            }
                        )
                    }
                }
                2 -> RulesScreen()
                3 -> AuthorsScreen()
                4 -> GameSettingsScreen()
            }
        }
    }
}