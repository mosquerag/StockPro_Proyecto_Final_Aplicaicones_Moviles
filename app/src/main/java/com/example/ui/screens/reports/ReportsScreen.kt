package com.example.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.StockProAppContainer
import com.example.ui.components.CurrencySelectorRow
import com.example.ui.components.FilterChipItem
import com.example.ui.theme.*
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.ReportsViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    currentCurrency: String = "COP",
    onCurrencyChanged: (String) -> Unit = {},
    viewModel: ReportsViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val reportState by viewModel.reportState.collectAsStateWithLifecycle()

    var selectedPeriod by remember { mutableStateOf("30 días") }
    var reportCurrency by remember { mutableStateOf(currentCurrency) }

    val periods = listOf(
        stringResource(R.string.period_7d),
        stringResource(R.string.period_30d),
        stringResource(R.string.period_3m)
    )

    val periodCode = remember(selectedPeriod) {
        when (selectedPeriod) {
            "7 días", "7 days" -> "7D"
            "3 meses", "3 months" -> "3M"
            else -> "30D"
        }
    }

    LaunchedEffect(periodCode, reportCurrency) {
        viewModel.loadReport(periodCode, reportCurrency)
    }

    val reportData = (reportState as? UiState.Success)?.data

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_reports), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Filtros de período (7 días, 30 días, 3 meses)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    periods.forEach { period ->
                        FilterChipItem(
                            label = period,
                            isSelected = selectedPeriod == period,
                            onClick = { selectedPeriod = period }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selector de Moneda de visualización (COP, USD, EUR)
                Text(
                    text = stringResource(R.string.report_currency_selector),
                    style = MaterialTheme.typography.labelMedium.copy(color = StockProTextSecondary)
                )
                Spacer(modifier = Modifier.height(6.dp))
                CurrencySelectorRow(
                    selectedCurrency = reportCurrency,
                    onCurrencySelected = {
                        reportCurrency = it
                        onCurrencyChanged(it)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (reportState is UiState.Loading && reportData == null) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = StockProGreenPrimary)
                    }
                }
            } else if (reportData != null) {
                // Resumen General
                item {
                    Text(
                        text = stringResource(R.string.lbl_general_summary),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ReportMetricRow(
                                label = stringResource(R.string.report_sales),
                                amount = Formatters.formatCurrency(BigDecimal.valueOf(reportData.ganancias.totalVentas), reportCurrency),
                                badge = "Ventas",
                                isPositive = true
                            )
                            ReportMetricRow(
                                label = stringResource(R.string.report_profits),
                                amount = Formatters.formatCurrency(BigDecimal.valueOf(reportData.ganancias.gananciaBruta), reportCurrency),
                                badge = "${reportData.ganancias.margenPorcentual.toInt()}% margen",
                                isPositive = true
                            )
                            ReportMetricRow(
                                label = stringResource(R.string.report_losses),
                                amount = Formatters.formatCurrency(BigDecimal.valueOf(reportData.perdidasYBajas.costoTotalPerdidas), reportCurrency),
                                badge = "${reportData.perdidasYBajas.totalUnidadesPerdidas.toInt()} bajas",
                                isPositive = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Sección Productos Más Vendidos
                item {
                    Text(
                        text = stringResource(R.string.report_best_sellers),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(reportData.masVendidos) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.productoNombre, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text(text = "${item.cantidadVendida.toInt()} unidades vendidas", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                            Text(
                                text = Formatters.formatCurrency(BigDecimal.valueOf(item.totalRecaudado), reportCurrency),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = StockProGreenDark)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Sección Menos Vendidos
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.report_worst_sellers),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(reportData.menosVendidos) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.productoNombre, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text(text = "${item.cantidadVendida.toInt()} unidades vendidas", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                            Text(
                                text = Formatters.formatCurrency(BigDecimal.valueOf(item.totalRecaudado), reportCurrency),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Sección Impuestos Cobrados
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.report_taxes_collected),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            reportData.impuestosCobrados.forEach { imp ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("${imp.categoriaFiscal} (${imp.tarifaPorcentaje.toInt()}%)", style = MaterialTheme.typography.bodyMedium)
                                    Text(Formatters.formatCurrency(BigDecimal.valueOf(imp.montoCobrado), reportCurrency), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportMetricRow(
    label: String,
    amount: String,
    badge: String,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(color = StockProTextSecondary))
            Text(text = amount, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }

        Surface(
            color = if (isPositive) StockProGreenContainer else StockProRedContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = badge,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isPositive) StockProGreenDark else StockProRedError,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
