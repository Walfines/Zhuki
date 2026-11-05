package com.example.Zhuki

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun UserSelectScreen(
    viewModel: GameViewModel = viewModel(),
    onUserSelected: (String) -> Unit
) {
    val users by viewModel.users.collectAsState()
    var selectedProfile by remember { mutableStateOf<ScoreEntity?>(null) }
    var selectedUser by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        Text("Выберите игрока", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { name ->
                val isSelected = (selectedUser == name)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedUser = name },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(onClick = {
                            scope.launch { selectedProfile = viewModel.getUserProfile(name) }
                        }) {
                            Text("⋮", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { if (selectedUser != null) onUserSelected(selectedUser!!) },
            enabled = selectedUser != null,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Играть", style = MaterialTheme.typography.titleLarge)
        }
    }

    // Окно с анкетой
    if (selectedProfile != null) {
        AlertDialog(
            onDismissRequest = { selectedProfile = null },
            title = { Text("Анкета: ${selectedProfile!!.userName}") },
            text = {
                Column {
                    Text("Пол: ${selectedProfile!!.gender}")
                    Text("Курс: ${selectedProfile!!.course}")
                    Text("Дата рождения: ${selectedProfile!!.birthDate}")
                    Text("Знак зодиака: ${selectedProfile!!.zodiacName}")
                    Spacer(Modifier.height(8.dp))
                    Text("Рекорд: ${selectedProfile!!.score}", style = MaterialTheme.typography.titleMedium)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedProfile = null }) { Text("Закрыть") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // При нажатии удалить, открываем второе окно подтверждения
                        showDeleteConfirm = selectedProfile!!.userName
                        selectedProfile = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Удалить игрока")
                }
            }
        )
    }

    // Окно подтверждения удаления
    if (showDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Удалить игрока?") },
            text = { Text("Вы уверены, что хотите удалить ${showDeleteConfirm} и всю его историю игр?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(showDeleteConfirm!!)
                        if (selectedUser == showDeleteConfirm) selectedUser = null
                        showDeleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Да, удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) { Text("Отмена") }
            }
        )
    }
}