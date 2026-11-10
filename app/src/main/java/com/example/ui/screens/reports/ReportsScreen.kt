package com.example.ui.screens.reports

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.ui.components.StockProButton
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
    val context = LocalContext.current
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

    // Datos estáticos/calculados para el Gráfico Donut de Ventas por Categoría (mock/calculado)
    val categorySalesData = remember(reportData) {
        listOf(
            CategorySalesItem("Alimentos", 40.0, StockProGreenPrimary),
            CategorySalesItem("Ropa", 25.0, StockProBlue),
            CategorySalesItem("Higiene", 20.0, StockProYellowWarning),
            CategorySalesItem("Otros", 15.0, StockProPurple)
        )
    }

    val exportReportAction = {
        if (reportData != null) {
            val csv = StringBuilder()
            csv.append("REPORTE DE VENTAS Y GANANCIAS - STOCKPRO\n")
            csv.append("Periodo: $selectedPeriod | Moneda: $reportCurrency\n\n")
            csv.append("RESUMEN GENERAL\n")
            csv.append("Total Ventas,${Formatters.formatCurrency(BigDecimal.valueOf(reportData.ganancias.totalVentas), reportCurrency)}\n")
            csv.append("Ganancia Bruta,${Formatters.formatCurrency(BigDecimal.valueOf(reportData.ganancias.gananciaBruta), reportCurrency)}\n")
            csv.append("Margen Bruto,${reportData.ganancias.margenPorcentual}%\n")
            csv.append("Perdidas/Bajas,${Formatters.formatCurrency(BigDecimal.valueOf(reportData.perdidasYBajas.costoTotalPerdidas), reportCurrency)}\n\n")
            csv.append("PRODUCTOS MAS VENDIDOS\n")
            csv.append("Producto,Cantidad Vendida,Total Recaudado\n")
            reportData.masVendidos.forEach { item ->
                csv.append("${item.productoNombre},${item.cantidadVendida.toInt()},${Formatters.formatCurrency(BigDecimal.valueOf(item.totalRecaudado), reportCurrency)}\n")
            }

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, csv.toString())
                putExtra(Intent.EXTRA_SUBJECT, "Reporte_StockPro_$periodCode.csv")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Exportar reporte con")
            context.startActivity(shareIntent)
        }
    }

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

                // Gráfica Donut de Ventas por Categoría (Punto 5)
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    CategorySalesPieChart(categoryData = categorySalesData)
                }

                // Botón Exportar Reporte
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    StockProButton(
                        text = "Exportar reporte",
                        leadingIcon = Icons.Default.Share,
                        onClick = exportReportAction
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun CategorySalesPieChart(
    categoryData: List<CategorySalesItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Ventas por categoría",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Gráfica Circular / Donut
                Canvas(modifier = Modifier.size(130.dp)) {
                    val strokeWidth = 28.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                    val arcSize = Size(diameter, diameter)

                    var startAngle = -90f
                    val totalPercentage = categoryData.sumOf { it.percentage }.toFloat().coerceAtLeast(1f)

                    categoryData.forEach { item ->
                        val sweepAngle = (item.percentage.toFloat() / totalPercentage) * 360f
                        drawArc(
                            color = item.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )
                        startAngle += sweepAngle
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Leyenda explicativa con nombres y porcentajes
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryData.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.width(140.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(item.color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.categoryName,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Text(
                                text = "${item.percentage.toInt()}%",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class CategorySalesItem(
    val categoryName: String,
    val percentage: Double,
    val color: Color
)

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
