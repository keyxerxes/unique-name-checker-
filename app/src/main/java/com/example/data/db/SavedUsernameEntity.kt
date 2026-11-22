package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_usernames")
data class SavedUsernameEntity(
    @PrimaryKey val username: String,
    val availabilityScore: Int, // e.g. 85 (85% available across checked platforms)
    val totalChecked: Int,
    val availableCount: Int,
    val note: String = "",
    val categoryVibe: String = "",
    val savedAt: Long = System.currentTimeMillis()
)
