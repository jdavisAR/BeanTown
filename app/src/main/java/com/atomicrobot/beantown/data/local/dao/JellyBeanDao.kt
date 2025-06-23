package com.atomicrobot.beantown.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity

/**
 * Provides CRUD functions for [jellybeanentity] SQLite DB table.
 *
 * Concrete implementation provided by Room.
 */
@Dao
interface JellyBeanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(beans: List<JellyBeanEntity>)

    @Query("SELECT * FROM jellybeanentity ORDER BY beanId ASC")
    fun pagingSource(): PagingSource<Int, JellyBeanEntity>

    @Query("SELECT * FROM jellybeanentity WHERE beanId == :beanId")
    suspend fun getBeanForBeanId(beanId: Int): JellyBeanEntity?

    @Query("DELETE FROM jellybeanentity")
    suspend fun clearAll()
}