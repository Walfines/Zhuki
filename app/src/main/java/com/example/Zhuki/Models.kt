package com.example.Zhuki

// ====== Зодиак и игрок ======

data class Zodiac(
    val name: String,
    val iconRes: Int
)

data class PlayerData(
    val fio: String,
    val gender: String,
    val course: String,
    val difficulty: Int,
    val birthDate: String,
    val zodiac: Zodiac
)

// ====== Игра «Жуки» ======

enum class BugType(
    val displayName: String,
    val score: Int,
    val speedFactor: Float,
    val sizeDp: Int,
    val iconRes: Int
) {
    COMMON("Обычный", 1, 1.0f, 56, R.drawable.bug_common),
    FAST("Быстрый", 3, 1.8f, 40, R.drawable.bug_fast),
    RARE("Редкий", 5, 1.2f, 72, R.drawable.bug_rare)
}

data class Bug(
    val id: Long,
    val type: BugType,
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float
)

data class GameStats(
    val score: Int = 0,
    val hits: Int = 0,
    val misses: Int = 0
) {
    val accuracy: Float
        get() = if (hits + misses == 0) 0f
        else hits.toFloat() / (hits + misses) * 100f
}