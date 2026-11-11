package com.example.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ProductEntity::class, DashboardCacheEntity::class],
    version = 2,
    exportSchema = false
)
abstract class StockProDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun dashboardDao(): DashboardDao

    companion object {
        @Volatile
        private var INSTANCE: StockProDatabase? = null

        fun getInstance(context: Context): StockProDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockProDatabase::class.java,
                    "stockpro_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
