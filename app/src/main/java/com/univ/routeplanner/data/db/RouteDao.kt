package com.univ.routeplanner.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(route: RouteEntity): Long

    @Query("SELECT * FROM routes ORDER BY fetchedAt DESC LIMIT 1")
    suspend fun getLatestRoute(): RouteEntity?

    @Query("DELETE FROM routes")
    suspend fun clearAll()
}