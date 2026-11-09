package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.R
import com.example.ui.screens.alerts.AlertsScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.inbound.CreateInboundScreen
import com.example.ui.screens.inbound.InboundDeliveriesScreen
import com.example.ui.screens.movements.MovementsScreen
import com.example.ui.screens.movements.RecordMovementScreen
import com.example.ui.screens.products.CategoriesScreen
import com.example.ui.screens.products.NewEditProductScreen
import com.example.ui.screens.products.ProductDetailScreen
import com.example.ui.screens.products.ProductsScreen
import com.example.ui.screens.reports.ReportsScreen
import com.example.ui.screens.sales.RegisterSaleScreen
import com.example.ui.screens.sales.SaleDetailScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.StockProGreenDark
import com.example.ui.theme.StockProGreenPrimary

data class BottomNavItem(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun StockProApp(
    currentLanguage: String,
    onLanguageChanged: (String) -> Unit
) {
    val navController = rememberNavController()
    var currentCurrency by remember { mutableStateOf("COP") }

    val bottomNavItems = listOf(
        BottomNavItem(NavRoute.Dashboard.route, R.string.nav_home, Icons.Default.Home, "bottom_nav_home"),
        BottomNavItem(NavRoute.Products.route, R.string.nav_products, Icons.Default.Inventory2, "bottom_nav_products"),
        BottomNavItem(NavRoute.Movements.route, R.string.nav_movements, Icons.Default.SyncAlt, "bottom_nav_movements"),
        BottomNavItem(NavRoute.Reports.route, R.string.nav_reports, Icons.Default.BarChart, "bottom_nav_reports"),
        BottomNavItem(NavRoute.Settings.route, R.string.nav_more, Icons.Default.MoreHoriz, "bottom_nav_more")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isTopLevelRoute = currentRoute in listOf(
        NavRoute.Dashboard.route,
        NavRoute.Products.route,
        NavRoute.Movements.route,
        NavRoute.Reports.route,
        NavRoute.Settings.route
    )

    val context = androidx.compose.ui.platform.LocalContext.current
    val tokenManager = remember { com.example.data.local.security.TokenManager(context) }
    val startDestination = remember {
        if (tokenManager.isLoggedIn()) NavRoute.Dashboard.route else NavRoute.Login.route
    }

    Scaffold(
        bottomBar = {
            if (isTopLevelRoute) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.titleRes),
                                    tint = if (selected) StockProGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(item.titleRes),
                                    color = if (selected) StockProGreenDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = StockProGreenPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoute.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(NavRoute.Dashboard.route) {
                            popUpTo(NavRoute.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(NavRoute.Register.route) }
                )
            }

            composable(NavRoute.Register.route) {
                RegisterScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(NavRoute.Dashboard.route) {
                            popUpTo(NavRoute.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoute.Dashboard.route) {
                DashboardScreen(
                    onNavigateToProducts = { navController.navigate(NavRoute.Products.route) },
                    onNavigateToSales = { navController.navigate(NavRoute.RegisterSale.route) },
                    onNavigateToAlerts = { navController.navigate(NavRoute.Alerts.route) },
                    onNavigateToInbound = { navController.navigate(NavRoute.InboundDeliveries.route) },
                    businessName = tokenManager.getBusinessName() ?: "Mi Tienda",
                    currencyCode = currentCurrency
                )
            }

            composable(NavRoute.Products.route) {
                ProductsScreen(
                    onNavigateToNewProduct = { navController.navigate(NavRoute.NewProduct.route) },
                    onNavigateToProductDetail = { id -> navController.navigate(NavRoute.ProductDetail.createRoute(id)) },
                    currencyCode = currentCurrency
                )
            }

            composable(NavRoute.NewProduct.route) {
                NewEditProductScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            composable(
                route = NavRoute.EditProduct.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStack ->
                val id = backStack.arguments?.getString("productId")
                NewEditProductScreen(
                    productId = id,
                    onNavigateBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            composable(
                route = NavRoute.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStack ->
                val id = backStack.arguments?.getString("productId") ?: ""
                ProductDetailScreen(
                    productId = id,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { prodId -> navController.navigate(NavRoute.EditProduct.createRoute(prodId)) },
                    currencyCode = currentCurrency
                )
            }

            composable(NavRoute.Categories.route) {
                CategoriesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoute.Movements.route) {
                MovementsScreen(
                    onNavigateToRegisterSale = { navController.navigate(NavRoute.RegisterSale.route) },
                    onNavigateToRecordMovement = { type -> navController.navigate(NavRoute.RecordMovement.createRoute(type)) },
                    currencyCode = currentCurrency
                )
            }

            composable(
                route = NavRoute.RecordMovement.route,
                arguments = listOf(navArgument("initialType") { type = NavType.StringType; defaultValue = "ENTRADA" })
            ) { backStack ->
                val type = backStack.arguments?.getString("initialType") ?: "ENTRADA"
                RecordMovementScreen(
                    initialType = type,
                    onNavigateBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            composable(NavRoute.RegisterSale.route) {
                RegisterSaleScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSaleCompleted = { saleId -> navController.navigate(NavRoute.SaleDetail.createRoute(saleId)) },
                    currencyCode = currentCurrency
                )
            }

            composable(
                route = NavRoute.SaleDetail.route,
                arguments = listOf(navArgument("saleId") { type = NavType.StringType })
            ) { backStack ->
                val id = backStack.arguments?.getString("saleId") ?: ""
                SaleDetailScreen(
                    saleId = id,
                    onNavigateBack = { navController.popBackStack() },
                    currencyCode = currentCurrency
                )
            }

            composable(NavRoute.InboundDeliveries.route) {
                InboundDeliveriesScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCreate = { navController.navigate(NavRoute.CreateInbound.route) },
                    currencyCode = currentCurrency
                )
            }

            composable(NavRoute.CreateInbound.route) {
                CreateInboundScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            composable(NavRoute.Alerts.route) {
                AlertsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoute.Reports.route) {
                ReportsScreen(
                    currentCurrency = currentCurrency,
                    onCurrencyChanged = { currentCurrency = it }
                )
            }

            composable(NavRoute.Settings.route) {
                SettingsScreen(
                    onNavigateToCategories = { navController.navigate(NavRoute.Categories.route) },
                    onNavigateToInbound = { navController.navigate(NavRoute.InboundDeliveries.route) },
                    onLogout = {
                        navController.navigate(NavRoute.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    currentLanguage = currentLanguage,
                    onLanguageChanged = onLanguageChanged
                )
            }
        }
    }
}
