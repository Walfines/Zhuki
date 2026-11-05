package com.example.Zhuki

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun GameScreen(
    speedMultiplier: Float,
    maxRoaches: Int,
    roundDurationSec: Int,
    onGameOver: (GameStats) -> Unit
) {
    var stats by remember { mutableStateOf(GameStats()) }
    var bugs by remember { mutableStateOf<List<Bug>>(emptyList()) }
    var timeLeft by remember { mutableIntStateOf(roundDurationSec) }
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }
    var nextId by remember { mutableLongStateOf(0L) }
    var gameFinished by remember { mutableStateOf(false) }

    val currentOnGameOver by rememberUpdatedState(onGameOver)

    // Таймер
    LaunchedEffect(Unit) {
        while (timeLeft > 0 && !gameFinished) {
            delay(1000)
            timeLeft--
        }
        if (!gameFinished) {
            gameFinished = true
            currentOnGameOver(stats)
        }
    }

    // Спавн
    LaunchedEffect(maxRoaches, gameFinished) {
        if (gameFinished) return@LaunchedEffect
        while (isActive && !gameFinished) {
            if (bugs.size < maxRoaches) {
                bugs = bugs + spawnBug(nextId++, speedMultiplier)
            }
            delay(700)
        }
    }

    // Движение
    LaunchedEffect(gameFinished) {
        if (gameFinished) return@LaunchedEffect
        while (isActive && !gameFinished) {
            delay(16)
            val newList = ArrayList<Bug>(bugs.size)
            for (bug in bugs) {
                var nx = bug.x + bug.vx
                var ny = bug.y + bug.vy
                var nvx = bug.vx
                var nvy = bug.vy
                if (nx < 0f || nx > 1f) { nvx = -nvx; nx = nx.coerceIn(0f, 1f) }
                if (ny < 0f || ny > 1f) { nvy = -nvy; ny = ny.coerceIn(0f, 1f) }
                newList.add(bug.copy(x = nx, y = ny, vx = nvx, vy = nvy))
            }
            bugs = newList
        }
    }

    // ПРИ ПЕРЕКЛЮЧЕНИИ ВКЛАДКИ ИГРА ПРЕРЫВАЕТСЯ
    DisposableEffect(Unit) {
        onDispose {
            if (!gameFinished) {
                gameFinished = true
                currentOnGameOver(stats)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { fieldSize = it }
            .pointerInput(gameFinished) {
                if (gameFinished) return@pointerInput
                detectTapGestures { offset ->
                    val hit = bugs.firstOrNull { bug ->
                        val bx = bug.x * fieldSize.width
                        val by = bug.y * fieldSize.height
                        val r = with(density) { bug.type.sizeDp.dp.toPx() } / 2
                        val dx = offset.x - bx
                        val dy = offset.y - by
                        dx * dx + dy * dy <= r * r
                    }
                    if (hit != null) {
                        stats = stats.copy(score = stats.score + hit.type.score, hits = stats.hits + 1)
                        bugs = bugs - hit
                    } else {
                        stats = stats.copy(score = (stats.score - 1).coerceAtLeast(0), misses = stats.misses + 1)
                    }
                }
            }
    ) {
        val density = LocalDensity.current
        bugs.forEach { bug ->
            val pxDp = with(density) { (bug.x * fieldSize.width).toDp() }
            val pyDp = with(density) { (bug.y * fieldSize.height).toDp() }
            Box(
                modifier = Modifier.offset(x = pxDp - bug.type.sizeDp.dp / 2, y = pyDp - bug.type.sizeDp.dp / 2)
            ) {
                Image(
                    painter = painterResource(id = bug.type.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(bug.type.sizeDp.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Очки: ${stats.score}", style = MaterialTheme.typography.titleMedium)
            Text("Время: $timeLeft", style = MaterialTheme.typography.titleMedium)
        }
    }
}

private fun spawnBug(id: Long, speedMultiplier: Float): Bug {
    val rnd = Random.Default
    val type = when (rnd.nextInt(100)) {
        in 0..69 -> BugType.COMMON
        in 70..89 -> BugType.FAST
        else -> BugType.RARE
    }
    val baseSpeed = 0.004f * speedMultiplier * type.speedFactor
    val angle = rnd.nextFloat() * 2f * Math.PI.toFloat()
    return Bug(
        id = id, type = type, x = rnd.nextFloat(), y = rnd.nextFloat(),
        vx = cos(angle) * baseSpeed, vy = sin(angle) * baseSpeed
    )
}