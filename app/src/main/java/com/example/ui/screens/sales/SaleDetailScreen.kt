package com.example.ui.screens.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.StockProAppContainer
import com.example.ui.components.StockProButton
import com.example.ui.theme.StockProGreenContainer
import com.example.ui.theme.StockProGreenDark
import com.example.ui.theme.StockProGreenPrimary
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.SaleDetailViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleDetailScreen(
    saleId: String,
    onNavigateBack: () -> Unit,
    currencyCode: String = "COP",
    viewModel: SaleDetailViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val saleState by viewModel.saleState.collectAsStateWithLifecycle()

    LaunchedEffect(saleId) {
        viewModel.loadSale(saleId)
    }

    val sale = (saleState as? UiState.Success)?.data

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_sale_detail), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        if (saleState is UiState.Loading && sale == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = StockProGreenPrimary)
            }
        } else if (sale == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.empty_sales_title))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(StockProGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StockProGreenPrimary, modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.sale_voucher, sale.numeroComprobante),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = sale.fechaVenta,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = stringResource(R.string.lbl_selected_items), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                        sale.items.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${item.productoNombre ?: "Producto"} (x${item.cantidad.toInt()} un.)", style = MaterialTheme.typography.bodyMedium)
                                Text(Formatters.formatCurrency(BigDecimal.valueOf(item.totalLinea), currencyCode), fontWeight = FontWeight.SemiBold)
                            }
                        }

                        HorizontalDivider()

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.lbl_subtotal), style = MaterialTheme.typography.bodyMedium)
                            Text(Formatters.formatCurrency(BigDecimal.valueOf(sale.subtotalBase), currencyCode), fontWeight = FontWeight.Medium)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.lbl_tax), style = MaterialTheme.typography.bodyMedium)
                            Text(Formatters.formatCurrency(BigDecimal.valueOf(sale.totalImpuestos), currencyCode), fontWeight = FontWeight.Medium)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.lbl_payment_method), style = MaterialTheme.typography.bodyMedium)
                            Text(sale.metodoPago, fontWeight = FontWeight.Medium)
                        }

                        HorizontalDivider()

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.lbl_total, ""), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(
                                text = Formatters.formatCurrency(BigDecimal.valueOf(sale.totalVenta), currencyCode),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = StockProGreenDark)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                StockProButton(
                    text = stringResource(R.string.btn_close),
                    onClick = onNavigateBack
                )
            }
        }
    }
}
