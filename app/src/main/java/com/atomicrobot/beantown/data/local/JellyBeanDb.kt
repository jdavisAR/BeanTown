package com.atomicrobot.beantown.data.local

import Converters
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.atomicrobot.beantown.data.local.dao.JellyBeanDao
import com.atomicrobot.beantown.data.local.dao.JellyBeanRemoteKeyDao
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity
import com.atomicrobot.beantown.data.local.entity.JellyBeanRemoteKey

@Database(
    entities = [
        JellyBeanEntity::class,
        JellyBeanRemoteKey::class,
    ],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class JellyBeanDb : RoomDatabase() {

    abstract val jellyBeansDao: JellyBeanDao

    abstract val remoteKeysDao: JellyBeanRemoteKeyDao

    companion object {

        /**
         * Constructs instance of [JellyBeanDb] either on disk or in-memory (disappears on app close).
         */
        fun buildDB(
            context: Context,
            inMemory: Boolean = false,
        ): JellyBeanDb = if (inMemory) {
            Room.inMemoryDatabaseBuilder<JellyBeanDb>(
                context = context,
                klass = JellyBeanDb::class.java,
            )
        } else {
            Room.databaseBuilder<JellyBeanDb>(
                context = context,
                klass = JellyBeanDb::class.java,
                name = jellyBeanDBName,
            )
        }
            .fallbackToDestructiveMigration(true)
            .build()
    }
}

val jellyBeanDBName: String = "jellybean.db"