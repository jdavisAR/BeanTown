package com.atomicrobot.beantown.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class for modeling a page/key relationship for Jelly Beans ie the Paging library uses
 * this object to determine what pages have been loaded (cached) as well as the neighboring pages
 */
@Entity
data class JellyBeanRemoteKey(
    val prevPage: Int?,
    @PrimaryKey(autoGenerate = false)
    val currentPage: Int,
    val nextPage: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
