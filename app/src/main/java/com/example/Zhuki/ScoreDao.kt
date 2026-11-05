package com.example.Zhuki

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(score: ScoreEntity)

    @Query("""
        SELECT * FROM scores 
        WHERE id IN (
            SELECT id FROM scores AS s2 
            WHERE s2.userName = scores.userName 
            ORDER BY s2.score DESC, s2.accuracy DESC, s2.timestamp ASC 
            LIMIT 1
        ) 
        ORDER BY score DESC
    """)
    suspend fun getAllRecords(): List<ScoreEntity>

    @Query("SELECT userName FROM scores GROUP BY userName ORDER BY userName")
    suspend fun getAllUsers(): List<String>

    @Query("SELECT * FROM scores WHERE userName = :name ORDER BY timestamp DESC LIMIT 1")
    suspend fun getUserProfile(name: String): ScoreEntity?

    @Query("DELETE FROM scores WHERE userName = :name")
    suspend fun deleteUser(name: String)
}