package com.example.ui.screens.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.WarningAmber
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
import com.example.data.remote.dto.VencimientoAlertDto
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FilterChipItem
import com.example.ui.theme.*
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.AlertsViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AlertsViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val lowStockState by viewModel.lowStockState.collectAsStateWithLifecycle()
    val expiringLotsState by viewModel.expiringLotsState.collectAsStateWithLifecycle()
    val expiredLotsState by viewModel.expiredLotsState.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("Todas") }
    val filters = listOf(
        stringResource(R.string.filter_all_alerts),
        stringResource(R.string.filter_low_stock),
        stringResource(R.string.filter_expiring),
        stringResource(R.string.filter_expired)
    )

    var alertToWriteOff by remember { mutableStateOf<VencimientoAlertDto?>(null) }

    val lowStockProducts = (lowStockState as? UiState.Success)?.data ?: emptyList()
    val expiringLots = (expiringLotsState as? UiState.Success)?.data ?: emptyList()
    val expiredLots = (expiredLotsState as? UiState.Success)?.data ?: emptyList()

    val lowStockAlerts = lowStockProducts.map { prod ->
        AlertItemModel(
            id = prod.id,
            productName = prod.nombre,
            message = "Stock bajo: quedan ${prod.stockActual.toInt()} unidades (Mín: ${prod.stockMinimo.toInt()})",
            type = "STOCK_BAJO",
            isCritical = true,
            lote = null
        )
    }

    val expiringAlerts = expiringLots.map { lot ->
        AlertItemModel(
            id = lot.loteId,
            productName = lot.productoNombre,
            message = "Vence en ${lot.diasRestantes} días · Vence el: ${Formatters.formatDate(lot.fechaVencimiento)} (Lote: ${lot.numeroLote})",
            type = "POR_VENCER",
            isCritical = false,
            lote = lot
        )
    }

    val expiredAlerts = expiredLots.map { lot ->
        AlertItemModel(
            id = lot.loteId,
            productName = lot.productoNombre,
            message = "VENCIDO el: ${Formatters.formatDate(lot.fechaVencimiento)} · ${lot.cantidadDisponible.toInt()} un. afectadas (Lote: ${lot.numeroLote})",
            type = "VENCIDO",
            isCritical = true,
            lote = lot
        )
    }

    val allAlerts = remember(lowStockAlerts, expiringAlerts, expiredAlerts) {
        lowStockAlerts + expiringAlerts + expiredAlerts
    }

    val filteredAlerts = remember(selectedFilter, allAlerts) {
        allAlerts.filter { alert ->
            when (selectedFilter) {
                "Stock bajo", "Low stock" -> alert.type == "STOCK_BAJO"
                "Por vencer", "Expiring soon" -> alert.type == "POR_VENCER"
                "Vencidos", "Expired" -> alert.type == "VENCIDO"
                else -> true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_alerts), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(filters) { filter ->
                    FilterChipItem(
                        label = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredAlerts.isEmpty()) {
                EmptyStateView(
                    title = stringResource(R.string.alerts_empty_title),
                    description = stringResource(R.string.alerts_empty_desc),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredAlerts) { alert ->
                        val isCritical = alert.isCritical
                        val iconColor = if (isCritical) StockProRedError else StockProYellowWarning
                        val containerBg = if (isCritical) StockProRedContainer else StockProYellowContainer

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("alert_card_${alert.id}"),
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
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(containerBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isCritical) Icons.Default.ErrorOutline else Icons.Default.WarningAmber,
                                        contentDescription = null,
                                        tint = iconColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alert.productName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = alert.message,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isCritical) StockProRedError else StockProYellowWarning,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }

                                if (alert.type == "VENCIDO" && alert.lote != null) {
                                    OutlinedButton(
                                        onClick = { alertToWriteOff = alert.lote },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StockProRedError),
                                        modifier = Modifier.testTag("write_off_alert_btn")
                                    ) {
                                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(stringResource(R.string.btn_write_off), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (alertToWriteOff != null) {
        val target = alertToWriteOff!!
        AlertDialog(
            onDismissRequest = { alertToWriteOff = null },
            title = { Text(stringResource(R.string.write_off_confirm_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.write_off_confirm_desc)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.darDeBajaLote(target.loteId, "Baja por fecha de vencimiento expirada")
                        alertToWriteOff = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StockProRedError)
                ) {
                    Text(stringResource(R.string.btn_confirm), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { alertToWriteOff = null }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

private data class AlertItemModel(
    val id: String,
    val productName: String,
    val message: String,
    val type: String,
    val isCritical: Boolean,
    val lote: VencimientoAlertDto?
)
