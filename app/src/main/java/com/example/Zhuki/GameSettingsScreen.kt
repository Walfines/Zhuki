package com.example.Zhuki

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private const val PREFS_NAME = "game_settings"
private const val KEY_SPEED = "speed"
private const val KEY_MAX_ROACHES = "maxRoaches"
private const val KEY_BONUS_INTERVAL = "bonusInterval"
private const val KEY_ROUND_DURATION = "roundDuration"

@Composable
fun GameSettingsScreen() {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var speed by remember { mutableFloatStateOf(prefs.getFloat(KEY_SPEED, 1f)) }
    var maxRoaches by remember { mutableFloatStateOf(prefs.getFloat(KEY_MAX_ROACHES, 10f)) }
    var bonusInterval by remember { mutableFloatStateOf(prefs.getFloat(KEY_BONUS_INTERVAL, 15f)) }
    var roundDuration by remember { mutableFloatStateOf(prefs.getFloat(KEY_ROUND_DURATION, 60f)) }

    var savedMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Настройки игры", style = MaterialTheme.typography.headlineMedium)

        Text("Скорость игры: ${"%.1f".format(speed)}x")
        Slider(
            value = speed,
            onValueChange = { speed = it },
            valueRange = 0.5f..3f,
            steps = 4
        )

        Text("Максимум тараканов на экране: ${maxRoaches.toInt()}")
        Slider(
            value = maxRoaches,
            onValueChange = { maxRoaches = it },
            valueRange = 1f..30f,
            steps = 28
        )

        Text("Интервал появления бонусов: ${bonusInterval.toInt()} сек")
        Slider(
            value = bonusInterval,
            onValueChange = { bonusInterval = it },
            valueRange = 5f..60f,
            steps = 10
        )

        Text("Длительность раунда: ${roundDuration.toInt()} сек")
        Slider(
            value = roundDuration,
            onValueChange = { roundDuration = it },
            valueRange = 30f..300f,
            steps = 8
        )

        Button(
            onClick = {
                prefs.edit()
                    .putFloat(KEY_SPEED, speed)
                    .putFloat(KEY_MAX_ROACHES, maxRoaches)
                    .putFloat(KEY_BONUS_INTERVAL, bonusInterval)
                    .putFloat(KEY_ROUND_DURATION, roundDuration)
                    .apply()

                savedMessage = "Настройки сохранены"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить настройки")
        }

        savedMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}