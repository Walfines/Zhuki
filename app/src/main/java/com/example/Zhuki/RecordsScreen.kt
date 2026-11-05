package com.example.Zhuki

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RecordsScreen(viewModel: GameViewModel = viewModel()) {
    val records by viewModel.records.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Лучшие рекорды", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        if (records.isEmpty()) {
            Text("Пока нет рекордов. Сыграйте раунд!", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(records) { record ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = record.userName, style = MaterialTheme.typography.titleMedium)
                            Text("Очки: ${record.score}", style = MaterialTheme.typography.titleLarge)
                            Text("Сложность: ${record.difficulty} | Точность: ${"%.1f".format(record.accuracy)}%")
                        }
                    }
                }
            }
        }
    }
}