package com.example.Zhuki

import android.view.ViewGroup
import android.widget.CalendarView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDropdown(selectedCourse: String, courses: List<String>, onCourseSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedCourse, onValueChange = {}, readOnly = true,
            label = { Text("Курс (ComboBox)") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            courses.forEach { course ->
                DropdownMenuItem(text = { Text(course) }, onClick = { onCourseSelected(course); expanded = false })
            }
        }
    }
}

@Composable
fun RegistrationScreen(onPlayerRegistered: (String) -> Unit) {
    val context = LocalContext.current
    val gameViewModel: GameViewModel = viewModel()
    val prefs = remember { context.getSharedPreferences("game_settings", android.content.Context.MODE_PRIVATE) }

    var fio by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Мужской") }
    val genders = listOf("Мужской", "Женский")
    val courses = listOf("1 курс", "2 курс", "3 курс", "4 курс", "Магистратура")
    var selectedCourse by remember { mutableStateOf("1 курс") }
    var difficulty by remember { mutableFloatStateOf(1f) }

    val calendar = remember { Calendar.getInstance() }
    var birthDay by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var birthMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }
    var birthYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedDateStr by remember { mutableStateOf(String.format("%02d.%02d.%d", birthDay, birthMonth, birthYear)) }

    var calendarViewRef by remember { mutableStateOf<CalendarView?>(null) }
    var isUpdating by remember { mutableStateOf(false) }
    var dayInput by remember { mutableStateOf(birthDay.toString()) }
    var monthInput by remember { mutableStateOf(birthMonth.toString()) }
    var yearInput by remember { mutableStateOf(birthYear.toString()) }
    var dateError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(dayInput, monthInput, yearInput) {
        val d = dayInput.toIntOrNull(); val m = monthInput.toIntOrNull(); val y = yearInput.toIntOrNull()
        if (d == null || m == null || y == null) { dateError = "Введите числа"; return@LaunchedEffect }
        if (!isValidDate(d, m, y)) { dateError = "Некорректная дата"; return@LaunchedEffect }
        dateError = null
        birthDay = d; birthMonth = m; birthYear = y
        selectedDateStr = String.format("%02d.%02d.%d", d, m, y)
        if (!isUpdating) {
            isUpdating = true
            calendarViewRef?.setDate(Calendar.getInstance().apply { set(y, m - 1, d) }.timeInMillis, true, true)
            isUpdating = false
        }
    }

    var player by remember { mutableStateOf<PlayerData?>(null) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Регистрация игрока", style = MaterialTheme.typography.headlineMedium) }
        item { OutlinedTextField(value = fio, onValueChange = { fio = it }, label = { Text("ФИО") }, modifier = Modifier.fillMaxWidth()) }
        item {
            Text("Пол:")
            Row(modifier = Modifier.fillMaxWidth()) {
                genders.forEach { gender ->
                    Row(modifier = Modifier.selectable(selected = (gender == selectedGender), onClick = { selectedGender = gender }).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = (gender == selectedGender), onClick = { selectedGender = gender })
                        Text(gender, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        }
        item { CourseDropdown(selectedCourse, courses, { selectedCourse = it }) }
        item {
            Text("Уровень сложности: ${difficulty.toInt()}")
            Slider(value = difficulty, onValueChange = { difficulty = it }, valueRange = 1f..5f, steps = 3)
        }
        item { Text("Дата рождения:") }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = dayInput, onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) dayInput = it }, label = { Text("День") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = monthInput, onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) monthInput = it }, label = { Text("Месяц") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = yearInput, onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) yearInput = it }, label = { Text("Год") }, modifier = Modifier.weight(1.4f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
            dateError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.height(8.dp))
            Text("Текущая дата: $selectedDateStr")
        }
        item {
            AndroidView(
                modifier = Modifier.fillMaxWidth().height(320.dp),
                factory = { ctx ->
                    CalendarView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        setDate(Calendar.getInstance().apply { set(birthYear, birthMonth - 1, birthDay) }.timeInMillis, false, true)
                        setOnDateChangeListener { _, year, month, dayOfMonth ->
                            if (isUpdating) return@setOnDateChangeListener
                            isUpdating = true
                            birthYear = year; birthMonth = month + 1; birthDay = dayOfMonth
                            selectedDateStr = String.format("%02d.%02d.%d", dayOfMonth, month + 1, year)
                            dayInput = dayOfMonth.toString(); monthInput = (month + 1).toString(); yearInput = year.toString()
                            dateError = null; isUpdating = false
                        }
                        calendarViewRef = this
                    }
                }
            )
        }
        item {
            Button(
                onClick = {
                    if (dateError != null) return@Button
                    val playerName = fio.ifBlank { "Игрок" }
                    player = PlayerData(
                        fio = playerName, gender = selectedGender, course = selectedCourse,
                        difficulty = difficulty.toInt(), birthDate = selectedDateStr,
                        zodiac = getZodiac(birthDay, birthMonth)
                    )

                    prefs.edit().apply {
                        putString("user_gender", selectedGender)
                        putString("user_course", selectedCourse)
                        putString("user_birth", selectedDateStr)
                        putString("user_zodiac", getZodiac(birthDay, birthMonth).name)
                        apply()
                    }

                    gameViewModel.saveScore(
                        userName = playerName, score = 0, difficulty = difficulty.toInt(),
                        hits = 0, misses = 0, accuracy = 0f,
                        gender = selectedGender, course = selectedCourse,
                        birthDate = selectedDateStr, zodiacName = getZodiac(birthDay, birthMonth).name
                    )

                    onPlayerRegistered(playerName)
                },
                enabled = dateError == null, modifier = Modifier.fillMaxWidth()
            ) { Text("Зарегистрировать игрока") }
        }
        player?.let { p ->
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Данные занесены:", style = MaterialTheme.typography.titleMedium)
                            Text("ФИО: ${p.fio}"); Text("Пол: ${p.gender}"); Text("Курс: ${p.course}")
                            Text("Сложность: ${p.difficulty}"); Text("Дата: ${p.birthDate}"); Text("Зодиак: ${p.zodiac.name}")
                        }
                        Spacer(Modifier.width(16.dp))
                        Image(painter = painterResource(id = p.zodiac.iconRes), contentDescription = p.zodiac.name, modifier = Modifier.size(96.dp))
                    }
                }
            }
        }
    }
}