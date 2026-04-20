package com.univ.routeplanner.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val password: String, // Note: In a real app, never store plain text passwords.
    val fullName: String? = null
)
