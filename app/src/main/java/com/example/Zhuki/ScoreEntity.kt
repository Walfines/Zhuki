package com.example.Zhuki

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userName: String,
    val score: Int,
    val difficulty: Int,
    val hits: Int,
    val misses: Int,
    val accuracy: Float,
    val timestamp: Long = System.currentTimeMillis(),

    // Данные из анкеты
    val gender: String = "Не указан",
    val course: String = "Не указан",
    val birthDate: String = "Не указана",
    val zodiacName: String = "Неизвестно"
)