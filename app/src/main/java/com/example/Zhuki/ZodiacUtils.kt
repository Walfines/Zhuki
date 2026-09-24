package com.example.Zhuki
import java.util.Calendar

fun getZodiac(day: Int, month: Int): Zodiac {
    return when (month) {
        1 -> if (day < 20) Zodiac("Козерог", R.drawable.capricorn)
        else Zodiac("Водолей", R.drawable.aquarius)
        2 -> if (day < 19) Zodiac("Водолей", R.drawable.aquarius)
        else Zodiac("Рыбы", R.drawable.pisces)
        3 -> if (day < 21) Zodiac("Рыбы", R.drawable.pisces)
        else Zodiac("Овен", R.drawable.aries)
        4 -> if (day < 20) Zodiac("Овен", R.drawable.aries)
        else Zodiac("Телец", R.drawable.taurus)
        5 -> if (day < 21) Zodiac("Телец", R.drawable.taurus)
        else Zodiac("Близнецы", R.drawable.gemini)
        6 -> if (day < 21) Zodiac("Близнецы", R.drawable.gemini)
        else Zodiac("Рак", R.drawable.cancer)
        7 -> if (day < 23) Zodiac("Рак", R.drawable.cancer)
        else Zodiac("Лев", R.drawable.leo)
        8 -> if (day < 23) Zodiac("Лев", R.drawable.leo)
        else Zodiac("Дева", R.drawable.virgo)
        9 -> if (day < 23) Zodiac("Дева", R.drawable.virgo)
        else Zodiac("Весы", R.drawable.libra)
        10 -> if (day < 23) Zodiac("Весы", R.drawable.libra)
        else Zodiac("Скорпион", R.drawable.scorpio)
        11 -> if (day < 22) Zodiac("Скорпион", R.drawable.scorpio)
        else Zodiac("Стрелец", R.drawable.sagittarius)
        12 -> if (day < 22) Zodiac("Стрелец", R.drawable.sagittarius)
        else Zodiac("Козерог", R.drawable.capricorn)
        else -> Zodiac("Неизвестно", R.drawable.ic_launcher_foreground)
    }
}


fun isValidDate(day: Int, month: Int, year: Int): Boolean {
    if (year < 1900 || year > 2100) return false
    if (month !in 1..12) return false
    if (day !in 1..31) return false

    val cal = Calendar.getInstance()
    cal.isLenient = false
    return try {
        cal.set(year, month - 1, day)
        cal.get(Calendar.YEAR) == year &&
                cal.get(Calendar.MONTH) == month - 1 &&
                cal.get(Calendar.DAY_OF_MONTH) == day
    } catch (e: Exception) {
        false
    }
}

