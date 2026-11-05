package com.example.Zhuki

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.scoreDao()

    private val _records = MutableStateFlow<List<ScoreEntity>>(emptyList())
    val records: StateFlow<List<ScoreEntity>> = _records.asStateFlow()

    private val _users = MutableStateFlow<List<String>>(emptyList())
    val users: StateFlow<List<String>> = _users.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _records.value = dao.getAllRecords()
            _users.value = dao.getAllUsers()
        }
    }

    fun saveScore(
        userName: String, score: Int, difficulty: Int,
        hits: Int, misses: Int, accuracy: Float,
        gender: String, course: String, birthDate: String, zodiacName: String
    ) {
        viewModelScope.launch {
            dao.insert(
                ScoreEntity(
                    userName = userName, score = score, difficulty = difficulty,
                    hits = hits, misses = misses, accuracy = accuracy,
                    gender = gender, course = course, birthDate = birthDate, zodiacName = zodiacName
                )
            )
            refreshData()
        }
    }

    suspend fun getUserProfile(name: String): ScoreEntity? {
        return dao.getUserProfile(name)
    }

    fun deleteUser(name: String) {
        viewModelScope.launch {
            dao.deleteUser(name)
            refreshData()
        }
    }
}