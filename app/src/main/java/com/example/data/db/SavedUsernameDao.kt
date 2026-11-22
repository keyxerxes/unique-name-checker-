package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedUsernameDao {
    @Query("SELECT * FROM saved_usernames ORDER BY savedAt DESC")
    fun getAllSaved(): Flow<List<SavedUsernameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUsername(item: SavedUsernameEntity)

    @Query("DELETE FROM saved_usernames WHERE username = :username")
    suspend fun deleteByUsername(username: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_usernames WHERE username = :username)")
    suspend fun isSaved(username: String): Boolean
}
