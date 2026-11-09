package com.example.data.local.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {

    @Query("SELECT * FROM dashboard_cache WHERE id = 1 LIMIT 1")
    fun getDashboardCache(): Flow<DashboardCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(dashboard: DashboardCacheEntity)

    @Query("DELETE FROM dashboard_cache")
    suspend fun clear()
}
