package com.example.data.remote.api

import com.example.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface StockProApi {

    // ==========================================
    // 1. AUTENTICACIÓN Y NEGOCIO (/api/v1/auth)
    // ==========================================
    @POST("api/v1/auth/register")
    suspend fun registerBusiness(@Body request: RegisterBusinessRequestDto): Response<AuthResponseDto>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): Response<AuthResponseDto>

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<Map<String, String>>

    // ==========================================
    // 2. PRODUCTOS (/api/v1/products)
    // ==========================================
    @GET("api/v1/products")
    suspend fun getProducts(@Query("categoriaId") categoriaId: String? = null): Response<List<ProductoDto>>

    @GET("api/v1/products/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<ProductoDto>

    @POST("api/v1/products")
    suspend fun createProduct(@Body request: CreateProductoRequestDto): Response<ProductoDto>

    @PUT("api/v1/products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body request: UpdateProductoRequestDto): Response<ProductoDto>

    @DELETE("api/v1/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<Map<String, String>>

    @GET("api/v1/products/low-stock")
    suspend fun getLowStockProducts(): Response<List<ProductoDto>>

    // ==========================================
    // 3. CATEGORÍAS (/api/v1/categories)
    // ==========================================
    @GET("api/v1/categories")
    suspend fun getCategories(): Response<List<CategoriaDto>>

    @POST("api/v1/categories")
    suspend fun createCategory(@Body request: CreateCategoriaRequestDto): Response<CategoriaDto>

    @PUT("api/v1/categories/{id}")
    suspend fun updateCategory(@Path("id") id: String, @Body request: CreateCategoriaRequestDto): Response<CategoriaDto>

    @DELETE("api/v1/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<Map<String, String>>

    // ==========================================
    // 4. LOTES Y VENCIMIENTOS (/api/v1/lots)
    // ==========================================
    @GET("api/v1/lots/product/{productoId}")
    suspend fun getLotsByProduct(@Path("productoId") productoId: String): Response<List<LoteDto>>

    @POST("api/v1/lots")
    suspend fun createLot(@Body request: CreateLoteRequestDto): Response<LoteDto>

    @POST("api/v1/lots/{id}/baja")
    suspend fun darDeBajaLote(@Path("id") id: String, @Body request: BajaLoteRequestDto): Response<Map<String, String>>

    @GET("api/v1/lots/expiring")
    suspend fun getExpiringLots(): Response<List<VencimientoAlertDto>>

    @GET("api/v1/lots/expired")
    suspend fun getExpiredLots(): Response<List<VencimientoAlertDto>>

    // ==========================================
    // 5. VENTAS (/api/v1/sales)
    // ==========================================
    @POST("api/v1/sales")
    suspend fun createSale(@Body request: CreateVentaRequestDto): Response<VentaDto>

    @GET("api/v1/sales")
    suspend fun getSales(
        @Query("inicio") inicio: String? = null,
        @Query("fin") fin: String? = null
    ): Response<List<VentaDto>>

    @GET("api/v1/sales/{id}")
    suspend fun getSaleById(@Path("id") id: String): Response<VentaDto>

    // ==========================================
    // 6. MOVIMIENTOS DE INVENTARIO (/api/v1/inventory-movements)
    // ==========================================
    @POST("api/v1/inventory-movements")
    suspend fun createMovement(@Body request: CreateMovimientoRequestDto): Response<MovimientoInventarioDto>

    @GET("api/v1/inventory-movements")
    suspend fun getMovements(
        @Query("inicio") inicio: String? = null,
        @Query("fin") fin: String? = null
    ): Response<List<MovimientoInventarioDto>>

    // ==========================================
    // 7. ENTRADAS PREVISTAS (/api/v1/inbound-deliveries)
    // ==========================================
    @POST("api/v1/inbound-deliveries")
    suspend fun createInboundDelivery(@Body request: CreateEntradaPrevistaRequestDto): Response<EntradaPrevistaDto>

    @GET("api/v1/inbound-deliveries/today")
    suspend fun getInboundToday(): Response<List<EntradaPrevistaDto>>

    @GET("api/v1/inbound-deliveries/tomorrow")
    suspend fun getInboundTomorrow(): Response<List<EntradaPrevistaDto>>

    @GET("api/v1/inbound-deliveries/{id}")
    suspend fun getInboundById(@Path("id") id: String): Response<EntradaPrevistaDto>

    @POST("api/v1/inbound-deliveries/{id}/receive")
    suspend fun receiveInbound(
        @Path("id") id: String,
        @Body request: ReceiveEntradaRequestDto? = null
    ): Response<EntradaPrevistaDto>

    // ==========================================
    // 8. DASHBOARD (/api/v1/dashboard)
    // ==========================================
    @GET("api/v1/dashboard")
    suspend fun getDashboard(@Query("currency") currency: String? = null): Response<DashboardDto>

    // ==========================================
    // 9. REPORTES (/api/v1/reports)
    // ==========================================
    @GET("api/v1/reports")
    suspend fun getReport(
        @Query("period") period: String = "30D",
        @Query("currency") currency: String? = null
    ): Response<ReporteGeneralDto>

    // ==========================================
    // 10. TASAS DE CAMBIO (/api/v1/exchange-rates)
    // ==========================================
    @GET("api/v1/exchange-rates")
    suspend fun getExchangeRates(): Response<List<TasaCambioDto>>

    @POST("api/v1/exchange-rates/refresh")
    suspend fun refreshExchangeRates(): Response<List<TasaCambioDto>>

    @POST("api/v1/exchange-rates/manual")
    suspend fun setManualExchangeRate(@Body request: ManualTasaCambioRequestDto): Response<TasaCambioDto>

    // ==========================================
    // 11. TARIFAS DE IMPUESTOS (/api/v1/tax-rates)
    // ==========================================
    @GET("api/v1/tax-rates")
    suspend fun getTaxRates(@Query("country") country: String? = null): Response<List<TarifaImpuestoDto>>

    // ==========================================
    // 12. NEGOCIO (/api/v1/business)
    // ==========================================
    @GET("api/v1/business")
    suspend fun getBusiness(): Response<BusinessDetailsDto>

    @PUT("api/v1/business")
    suspend fun updateBusiness(@Body request: UpdateBusinessRequestDto): Response<BusinessDetailsDto>
}
