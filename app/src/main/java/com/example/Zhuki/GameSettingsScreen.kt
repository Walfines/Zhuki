package com.example.Zhuki

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameSettingsScreen() {
    var speed by remember { mutableFloatStateOf(1f) }
    var maxRoaches by remember { mutableFloatStateOf(10f) }
    var bonusInterval by remember { mutableFloatStateOf(15f) }
    var roundDuration by remember { mutableFloatStateOf(60f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Настройки игры", style = MaterialTheme.typography.headlineMedium)

        Text("Скорость игры: ${"%.1f".format(speed)}x")
        Slider(value = speed, onValueChange = { speed = it },
            valueRange = 0.5f..3f, steps = 4)

        Text("Максимум тараканов на экране: ${maxRoaches.toInt()}")
        Slider(value = maxRoaches, onValueChange = { maxRoaches = it },
            valueRange = 1f..30f, steps = 28)

        Text("Интервал появления бонусов: ${bonusInterval.toInt()} сек")
        Slider(value = bonusInterval, onValueChange = { bonusInterval = it },
            valueRange = 5f..60f, steps = 10)

        Text("Длительность раунда: ${roundDuration.toInt()} сек")
        Slider(value = roundDuration, onValueChange = { roundDuration = it },
            valueRange = 30f..300f, steps = 8)

        Button(onClick = { /* TODO: сохранить */ },
            modifier = Modifier.fillMaxWidth()) {
            Text("Сохранить настройки")
        }
    }
}
