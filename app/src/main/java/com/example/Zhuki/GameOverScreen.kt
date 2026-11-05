package com.example.Zhuki

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameOverScreen(
    stats: GameStats,
    onRestart: () -> Unit,
    onChangeUser: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Раунд окончен!", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(24.dp))
        Text("Очки: ${stats.score}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text("Попадания: ${stats.hits}")
        Text("Промахи: ${stats.misses}")
        Text("Точность: ${"%.1f".format(stats.accuracy)} %")
        Spacer(Modifier.height(32.dp))
        Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) { Text("Играть снова") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onChangeUser, modifier = Modifier.fillMaxWidth()) { Text("Сменить игрока") }
    }
}