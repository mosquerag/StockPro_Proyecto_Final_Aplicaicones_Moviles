package com.example.ui.mock

import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

data class MockProduct(
    val id: String,
    val name: String,
    val category: String,
    val costPrice: BigDecimal,
    val salePrice: BigDecimal,
    val stock: BigDecimal,
    val minStock: BigDecimal,
    val fiscalCategory: String,
    val managesLots: Boolean,
    val isLowStock: Boolean = false,
    val expirationDate: LocalDate? = null,
    val lots: List<MockLot> = emptyList()
)

data class MockLot(
    val id: String,
    val lotNumber: String,
    val available: BigDecimal,
    val expirationDate: LocalDate,
    val status: String // ACTIVO, AGOTADO, BAJA
)

data class MockMovement(
    val id: String,
    val productName: String,
    val type: String, // VENTA, ENTRADA, PERDIDA, DANADO
    val quantity: BigDecimal,
    val amountOrCost: BigDecimal,
    val reason: String,
    val timeAgo: String
)

data class MockInbound(
    val id: String,
    val reference: String,
    val expectedDate: LocalDate,
    val itemCount: Int,
    val estimatedTotal: BigDecimal,
    val status: String, // PENDIENTE, RECIBIDA
    val itemsSummary: String
)

data class MockAlert(
    val id: String,
    val productName: String,
    val type: String, // STOCK_BAJO, POR_VENCER, VENCIDO
    val message: String,
    val isCritical: Boolean
)

data class MockRankingItem(
    val name: String,
    val category: String,
    val units: BigDecimal,
    val total: BigDecimal
)

object MockDataProvider {

    val sampleLotsLeche = listOf(
        MockLot("l1", "LOT-202611-01", BigDecimal("5"), LocalDate.now().plusDays(2), "ACTIVO"),
        MockLot("l2", "LOT-202611-02", BigDecimal("15"), LocalDate.now().plusDays(15), "ACTIVO")
    )

    val sampleProducts = listOf(
        MockProduct("p1", "Leche entera 1L", "Alimentos", BigDecimal("2800"), BigDecimal("4000"), BigDecimal("20"), BigDecimal("5"), "EXCLUIDO", true, false, LocalDate.now().plusDays(2), sampleLotsLeche),
        MockProduct("p2", "Pan artesanal", "Alimentos", BigDecimal("1200"), BigDecimal("2500"), BigDecimal("3"), BigDecimal("10"), "EXCLUIDO", false, true),
        MockProduct("p3", "Camiseta deportiva", "Ropa", BigDecimal("18000"), BigDecimal("35000"), BigDecimal("10"), BigDecimal("2"), "GENERAL", false, false),
        MockProduct("p4", "Shampoo hidratante", "Higiene", BigDecimal("4500"), BigDecimal("8000"), BigDecimal("8"), BigDecimal("3"), "GENERAL", false, false),
        MockProduct("p5", "Manzana roja x kg", "Alimentos", BigDecimal("800"), BigDecimal("1500"), BigDecimal("25"), BigDecimal("5"), "EXCLUIDO", true, false, LocalDate.now().plusDays(4)),
        MockProduct("p6", "Yogur de fresa 200ml", "Alimentos", BigDecimal("1800"), BigDecimal("3200"), BigDecimal("10"), BigDecimal("4"), "GENERAL", true, false, LocalDate.now().plusDays(3))
    )

    val sampleMovements = listOf(
        MockMovement("m1", "Leche entera 1L", "VENTA", BigDecimal("2"), BigDecimal("8000"), "Venta mostrador V-20261103-A1", "Hoy, 10:24"),
        MockMovement("m2", "Pan artesanal", "ENTRADA", BigDecimal("10"), BigDecimal("12000"), "Recepción orden ENT-20261102", "Ayer, 16:30"),
        MockMovement("m3", "Manzana roja x kg", "PERDIDA", BigDecimal("5"), BigDecimal("4000"), "Fruta magullada en exhibición", "Ayer, 12:15"),
        MockMovement("m4", "Yogur de fresa 200ml", "DANADO", BigDecimal("2"), BigDecimal("3600"), "Empaque perforado durante descarga", "01 Nov, 09:00")
    )

    val sampleInboundsToday = listOf(
        MockInbound("in1", "ENT-20261103-A1 [Distribuidora Lácteos]", LocalDate.now(), 2, BigDecimal("180000"), "PENDIENTE", "Leche entera (50 un.), Yogur (30 un.)")
    )

    val sampleInboundsTomorrow = listOf(
        MockInbound("in2", "ENT-20261104-B2 [Panadería Central]", LocalDate.now().plusDays(1), 1, BigDecimal("48000"), "PENDIENTE", "Pan artesanal (40 un.)")
    )

    val sampleAlerts = listOf(
        MockAlert("a1", "Leche entera 1L", "POR_VENCER", "Vence en 2 días (Lote: LOT-202611-01)", false),
        MockAlert("a2", "Pan artesanal", "STOCK_BAJO", "Stock bajo: solo quedan 3 unidades (mínimo 10)", true),
        MockAlert("a3", "Yogur de fresa 200ml", "POR_VENCER", "Vence en 3 días (Lote: LOT-202611-03)", false),
        MockAlert("a4", "Manzana roja x kg", "VENCIDO", "Ya venció ayer (Lote: LOT-202610-99)", true)
    )

    val sampleBestSellers = listOf(
        MockRankingItem("Leche entera 1L", "Alimentos", BigDecimal("42"), BigDecimal("168000")),
        MockRankingItem("Pan artesanal", "Alimentos", BigDecimal("35"), BigDecimal("87500")),
        MockRankingItem("Camiseta deportiva", "Ropa", BigDecimal("18"), BigDecimal("630000"))
    )

    val sampleWorstSellers = listOf(
        MockRankingItem("Shampoo hidratante", "Higiene", BigDecimal("2"), BigDecimal("16000")),
        MockRankingItem("Manzana roja x kg", "Alimentos", BigDecimal("5"), BigDecimal("7500"))
    )
}
