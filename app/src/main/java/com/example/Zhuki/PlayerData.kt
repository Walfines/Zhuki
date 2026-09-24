package com.example.Zhuki


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