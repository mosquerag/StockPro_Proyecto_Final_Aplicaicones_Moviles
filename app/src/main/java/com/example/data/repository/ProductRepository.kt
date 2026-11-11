package com.example.data.repository

import com.example.data.local.db.ProductDao
import com.example.data.local.db.ProductEntity
import com.example.data.remote.ApiClient
import com.example.data.remote.api.StockProApi
import com.example.data.remote.dto.CreateProductoRequestDto
import com.example.data.remote.dto.ProductoDto
import com.example.data.remote.dto.UpdateProductoRequestDto
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val api: StockProApi,
    private val productDao: ProductDao
) {

    // Flujo reactivo desde Room local
    val cachedProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    fun getCachedProductsByCategory(categoryId: String): Flow<List<ProductEntity>> =
        productDao.getProductsByCategory(categoryId)

    fun getCachedProductById(productId: String): Flow<ProductEntity?> =
        productDao.getProductById(productId)

    fun getCachedLowStockProducts(): Flow<List<ProductEntity>> =
        productDao.getLowStockProducts()

    /**
     * Sincroniza productos desde el backend y actualiza la caché local de Room
     */
    suspend fun refreshProducts(categoriaId: String? = null): Result<List<ProductoDto>> {
        return try {
            val response = api.getProducts(categoriaId)
            if (response.isSuccessful && response.body() != null) {
                val dtoList = response.body()!!
                // Si estamos trayendo todos los productos, actualizamos la base de datos local
                if (categoriaId == null) {
                    val entities = dtoList.map { dto ->
                        ProductEntity(
                            id = dto.id,
                            negocioId = dto.negocioId,
                            categoriaId = dto.categoriaId,
                            categoriaNombre = dto.categoriaNombre,
                            nombre = dto.nombre,
                            codigoBarras = dto.codigoBarras,
                            unidadMedida = dto.unidadMedida,
                            costo = dto.costo,
                            precioVenta = dto.precioVenta,
                            categoriaFiscal = dto.categoriaFiscal,
                            stockActual = dto.stockActual,
                            stockMinimo = dto.stockMinimo,
                            manejaLotes = dto.manejaLotes,
                            imagenUrl = dto.imagenUrl,
                            stockBajo = dto.stockBajo,
                            activo = dto.activo
                        )
                    }
                    productDao.clearProducts()
                    productDao.insertProducts(entities)
                }
                Result.success(dtoList)
            } else {
                val error = ApiClient.parseError(response)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: String): Result<ProductoDto> {
        return try {
            val response = api.getProductById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduct(request: CreateProductoRequestDto): Result<ProductoDto> {
        return try {
            val response = api.createProduct(request)
            if (response.isSuccessful && response.body() != null) {
                val created = response.body()!!
                // Guardar en Room
                productDao.insertProduct(
                    ProductEntity(
                        id = created.id,
                        negocioId = created.negocioId,
                        categoriaId = created.categoriaId,
                        categoriaNombre = created.categoriaNombre,
                        nombre = created.nombre,
                        codigoBarras = created.codigoBarras,
                        unidadMedida = created.unidadMedida,
                        costo = created.costo,
                        precioVenta = created.precioVenta,
                        categoriaFiscal = created.categoriaFiscal,
                        stockActual = created.stockActual,
                        stockMinimo = created.stockMinimo,
                        manejaLotes = created.manejaLotes,
                        imagenUrl = created.imagenUrl,
                        stockBajo = created.stockBajo,
                        activo = created.activo
                    )
                )
                Result.success(created)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(id: String, request: UpdateProductoRequestDto): Result<ProductoDto> {
        return try {
            val response = api.updateProduct(id, request)
            if (response.isSuccessful && response.body() != null) {
                val updated = response.body()!!
                productDao.insertProduct(
                    ProductEntity(
                        id = updated.id,
                        negocioId = updated.negocioId,
                        categoriaId = updated.categoriaId,
                        categoriaNombre = updated.categoriaNombre,
                        nombre = updated.nombre,
                        codigoBarras = updated.codigoBarras,
                        unidadMedida = updated.unidadMedida,
                        costo = updated.costo,
                        precioVenta = updated.precioVenta,
                        categoriaFiscal = updated.categoriaFiscal,
                        stockActual = updated.stockActual,
                        stockMinimo = updated.stockMinimo,
                        manejaLotes = updated.manejaLotes,
                        imagenUrl = updated.imagenUrl,
                        stockBajo = updated.stockBajo,
                        activo = updated.activo
                    )
                )
                Result.success(updated)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: String): Result<Unit> {
        return try {
            val response = api.deleteProduct(id)
            if (response.isSuccessful) {
                productDao.deleteProductById(id)
                Result.success(Unit)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLowStockProducts(): Result<List<ProductoDto>> {
        return try {
            val response = api.getLowStockProducts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
