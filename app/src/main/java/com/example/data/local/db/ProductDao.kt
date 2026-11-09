package com.example.data.local.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products_cache WHERE activo = 1 ORDER BY nombre ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products_cache WHERE categoriaId = :categoryId AND activo = 1 ORDER BY nombre ASC")
    fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products_cache WHERE id = :productId LIMIT 1")
    fun getProductById(productId: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products_cache WHERE stockBajo = 1 AND activo = 1 ORDER BY stockActual ASC")
    fun getLowStockProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Query("DELETE FROM products_cache")
    suspend fun clearProducts()

    @Query("DELETE FROM products_cache WHERE id = :productId")
    suspend fun deleteProductById(productId: String)
}
