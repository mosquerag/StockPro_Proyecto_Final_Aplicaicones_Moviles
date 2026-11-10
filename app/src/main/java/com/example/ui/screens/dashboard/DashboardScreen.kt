package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.StockProAppContainer
import com.example.ui.components.FilterChipItem
import com.example.ui.components.SummaryMetricCard
import com.example.ui.theme.*
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState
import java.math.BigDecimal

@Composable
fun DashboardScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToInbound: () -> Unit,
    businessName: String = "Mi Tienda",
    currencyCode: String = "COP",
    viewModel: DashboardViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val cachedDashboard by viewModel.cachedDashboard.collectAsStateWithLifecycle()
    val dashboardState by viewModel.dashboardState.collectAsStateWithLifecycle()

    var selectedPeriodicity by remember { mutableStateOf("Hoy") }
    val periods = listOf("Hoy", "Semanal", "Quincenal", "Mensual", "Anual")

    LaunchedEffect(currencyCode) {
        viewModel.loadDashboard(currencyCode)
    }

    val remoteData = (dashboardState as? UiState.Success)?.data
    val displayName = remoteData?.negocioNombre?.takeIf { it.isNotBlank() }
        ?: cachedDashboard?.negocioNombre?.takeIf { it.isNotBlank() }
        ?: businessName

    val baseEarnings = remoteData?.gananciasEstimadasHoy
        ?: cachedDashboard?.gananciasEstimadasHoy
        ?: 0.0

    // Factor multiplicador dinámico por periodicidad elegida
    val displayedEarnings = remember(selectedPeriodicity, baseEarnings) {
        when (selectedPeriodicity) {
            "Semanal" -> baseEarnings * 7.0
            "Quincenal" -> baseEarnings * 15.0
            "Mensual" -> baseEarnings * 30.0
            "Anual" -> baseEarnings * 365.0
            else -> baseEarnings
        }
    }

    val productCount = remoteData?.cantidadProductos
        ?: cachedDashboard?.cantidadProductos
        ?: 0L

    val salesTodayCount = remoteData?.ultimasVentas?.size
        ?: cachedDashboard?.ventasHoy?.toInt()
        ?: 0

    val expiringCount = remoteData?.alertas?.porVencerCount
        ?: cachedDashboard?.porVencerCount
        ?: 0

    val lowStockCount = remoteData?.alertas?.stockBajoCount
        ?: cachedDashboard?.stockBajoCount
        ?: 0

    val inboundsToday = remoteData?.entradasHoy ?: emptyList()
    val recentSales = remoteData?.ultimasVentas ?: emptyList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.greeting_prefix, displayName),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.dashboard_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Selector Interactivo de Periodicidad para Ganancias Estimadas
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                items(periods) { period ->
                    FilterChipItem(
                        label = period,
                        isSelected = selectedPeriodicity == period,
                        onClick = { selectedPeriodicity = period }
                    )
                }
            }
        }

        // Tarjeta Hero Verde de Ganancias Estimadas
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = StockProGreenPrimary)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Ganancias estimadas ($selectedPeriodicity)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatCurrency(BigDecimal.valueOf(displayedEarnings), currencyCode),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 32.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.earnings_badge),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Cuadrícula 2x2 de Tarjetas de Resumen
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryMetricCard(
                    title = stringResource(R.string.card_products),
                    value = productCount.toString(),
                    icon = Icons.Default.Inventory2,
                    iconColor = StockProBlue,
                    iconContainerColor = StockProBlueContainer,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToProducts
                )
                SummaryMetricCard(
                    title = stringResource(R.string.card_sales_today),
                    value = salesTodayCount.toString(),
                    icon = Icons.Default.ShoppingCart,
                    iconColor = StockProGreenPrimary,
                    iconContainerColor = StockProGreenContainer,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSales
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryMetricCard(
                    title = stringResource(R.string.card_expiring_soon),
                    value = expiringCount.toString(),
                    icon = Icons.Default.WarningAmber,
                    iconColor = StockProYellowWarning,
                    iconContainerColor = StockProYellowContainer,
                    modifier = Modifier.weight(1f),
                    badgeText = expiringCount.toString(),
                    isWarning = false,
                    onClick = onNavigateToAlerts
                )
                SummaryMetricCard(
                    title = stringResource(R.string.card_low_stock),
                    value = lowStockCount.toString(),
                    icon = Icons.Default.ErrorOutline,
                    iconColor = StockProRedError,
                    iconContainerColor = StockProRedContainer,
                    modifier = Modifier.weight(1f),
                    badgeText = lowStockCount.toString(),
                    isWarning = true,
                    onClick = onNavigateToAlerts
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Sección Entradas de Hoy
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.inbound_today),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = stringResource(R.string.view_all),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = StockProGreenDark,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.clickable(onClick = onNavigateToInbound)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (inboundsToday.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = stringResource(R.string.empty_inbound_title),
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(inboundsToday) { inbound ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToInbound),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(StockProBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = StockProBlue)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = inbound.numeroOrden,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "${inbound.items.size} ${stringResource(R.string.nav_products)}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Surface(
                            color = StockProYellowContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = inbound.estado,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF8F5800),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Sección Últimas Ventas
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.recent_sales),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = stringResource(R.string.view_all),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = StockProGreenDark,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.clickable(onClick = onNavigateToSales)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (recentSales.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = stringResource(R.string.empty_sales_title),
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(recentSales) { sale ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(StockProGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = StockProGreenPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = sale.numeroComprobante,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "${sale.metodoPago} · ${Formatters.formatDateTime(sale.fechaVenta)}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Text(
                            text = Formatters.formatCurrency(BigDecimal.valueOf(sale.totalVenta), currencyCode),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = StockProGreenDark
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
