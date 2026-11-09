package com.example.ui.navigation

sealed class NavRoute(val route: String) {
    object Login : NavRoute("login")
    object Register : NavRoute("register")
    object Dashboard : NavRoute("dashboard")
    object Products : NavRoute("products")
    object NewProduct : NavRoute("new_product")
    object EditProduct : NavRoute("edit_product/{productId}") {
        fun createRoute(productId: String) = "edit_product/$productId"
    }
    object ProductDetail : NavRoute("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Categories : NavRoute("categories")
    object Movements : NavRoute("movements")
    object RecordMovement : NavRoute("record_movement/{initialType}") {
        fun createRoute(initialType: String = "ENTRADA") = "record_movement/$initialType"
    }
    object RegisterSale : NavRoute("register_sale")
    object SaleDetail : NavRoute("sale_detail/{saleId}") {
        fun createRoute(saleId: String) = "sale_detail/$saleId"
    }
    object InboundDeliveries : NavRoute("inbound_deliveries")
    object CreateInbound : NavRoute("create_inbound")
    object Alerts : NavRoute("alerts")
    object Reports : NavRoute("reports")
    object Settings : NavRoute("settings")
}
