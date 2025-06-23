package com.atomicrobot.beantown.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atomicrobot.beantown.data.local.entity.JellyBeanRemoteKey

/**
 * Provides CRUD functions for [jellybeanremotekey] SQLite DB table.
 *
 * Concrete implementation provided by Room.
 */
@Dao
interface JellyBeanRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(remoteKey: JellyBeanRemoteKey)

    @Query("SELECT * FROM jellybeanremotekey where currentPage = :page")
    suspend fun getRemoteKeyForPage(page: Int): JellyBeanRemoteKey?

    @Query("DELETE FROM jellybeanremotekey")
    suspend fun clearAll()

    @Query("SELECT created_at FROM jellybeanremotekey Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}