package com.example.Zhuki

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val tabs = listOf("Регистрация", "Игра", "Правила", "Авторы", "Настройки", "Рекорды")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("game_settings", Context.MODE_PRIVATE) }

    val speed = prefs.getFloat("speed", 1f)
    val maxRoaches = prefs.getFloat("maxRoaches", 10f).toInt()
    val roundDuration = prefs.getFloat("roundDuration", 60f).toInt()

    val gameViewModel: GameViewModel = viewModel()

    var currentUser by remember { mutableStateOf(prefs.getString("current_user", null)) }
    var gameStats by remember { mutableStateOf<GameStats?>(null) }
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
                0 -> RegistrationScreen(
                    onPlayerRegistered = { name ->
                        // Просто сохраняем и НЕ переключаем вкладку
                        currentUser = name
                        prefs.edit().putString("current_user", name).apply()
                    }
                )
                1 -> {
                    if (currentUser == null) {
                        // Показываем только выбор игрока (без поля ввода)
                        UserSelectScreen(
                            viewModel = gameViewModel,
                            onUserSelected = { name ->
                                currentUser = name
                                prefs.edit().putString("current_user", name).apply()
                            }
                        )
                    } else {
                        val stats = gameStats
                        if (stats == null) {
                            key(gameKey) {
                                GameScreen(
                                    speedMultiplier = speed,
                                    maxRoaches = maxRoaches,
                                    roundDurationSec = roundDuration,
                                    onGameOver = { s ->
                                        gameViewModel.saveScore(
                                            userName = currentUser!!,
                                            score = s.score,
                                            difficulty = speed.toInt(),
                                            hits = s.hits, misses = s.misses, accuracy = s.accuracy,
                                            gender = prefs.getString("user_gender", "Не указан") ?: "Не указан",
                                            course = prefs.getString("user_course", "Не указан") ?: "Не указан",
                                            birthDate = prefs.getString("user_birth", "Не указана") ?: "Не указана",
                                            zodiacName = prefs.getString("user_zodiac", "Неизвестно") ?: "Неизвестно"
                                        )
                                        gameStats = s
                                    }
                                )
                            }
                        } else {
                            GameOverScreen(
                                stats = stats,
                                onRestart = { gameStats = null; gameKey++ },
                                onChangeUser = {
                                    currentUser = null
                                    prefs.edit().remove("current_user").apply()
                                    gameStats = null
                                    gameKey++
                                }
                            )
                        }
                    }
                }
                2 -> RulesScreen()
                3 -> AuthorsScreen()
                4 -> GameSettingsScreen()
                5 -> RecordsScreen(viewModel = gameViewModel)
            }
        }
    }
}