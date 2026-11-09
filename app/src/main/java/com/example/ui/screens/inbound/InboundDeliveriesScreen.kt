package com.example.ui.screens.inbound

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
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
import com.example.data.remote.dto.EntradaPrevistaDto
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.InboundViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboundDeliveriesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    currencyCode: String = "COP",
    viewModel: InboundViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val todayState by viewModel.todayState.collectAsStateWithLifecycle()
    val tomorrowState by viewModel.tomorrowState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) } // 0: Hoy, 1: Mañana
    var deliveryToReceive by remember { mutableStateOf<EntradaPrevistaDto?>(null) }

    val todayInbounds = (todayState as? UiState.Success)?.data ?: emptyList()
    val tomorrowInbounds = (tomorrowState as? UiState.Success)?.data ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_inbound_deliveries), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = StockProGreenPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_inbound_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.btn_new_inbound))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            // Tabs Hoy vs Mañana
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = StockProGreenPrimary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(stringResource(R.string.tab_today), fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(R.string.tab_tomorrow), fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val currentList = if (selectedTab == 0) todayInbounds else tomorrowInbounds
            val currentState = if (selectedTab == 0) todayState else tomorrowState

            if (currentState is UiState.Loading && currentList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = StockProGreenPrimary)
                }
            } else if (currentList.isEmpty()) {
                EmptyStateView(
                    title = if (selectedTab == 0) stringResource(R.string.no_inbound_today) else stringResource(R.string.no_inbound_tomorrow),
                    description = stringResource(R.string.empty_inbound_title),
                    actionButtonText = stringResource(R.string.btn_new_inbound),
                    onActionClick = onNavigateToCreate,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(currentList) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(StockProBlueContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = StockProBlue)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.numeroOrden,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = item.fechaPrevista,
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                    }
                                    Surface(
                                        color = if (item.estado == "RECIBIDA") StockProGreenContainer else StockProYellowContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = if (item.estado == "RECIBIDA") stringResource(R.string.status_received) else stringResource(R.string.status_pending),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (item.estado == "RECIBIDA") StockProGreenDark else Color(0xFF8F5800),
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                val summary = item.items.joinToString { "${it.productoNombre ?: "Producto"} (${it.cantidadEsperada.toInt()} un.)" }
                                Text(
                                    text = summary.ifBlank { stringResource(R.string.inbound_today) },
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Total estimado: ${Formatters.formatCurrency(BigDecimal.valueOf(item.totalEstimado), currencyCode)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = StockProGreenDark)
                                )

                                if (item.estado == "PENDIENTE") {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = { deliveryToReceive = item },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = StockProGreenPrimary),
                                        modifier = Modifier.fillMaxWidth().height(44.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(R.string.btn_receive_delivery), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (deliveryToReceive != null) {
        val target = deliveryToReceive!!
        AlertDialog(
            onDismissRequest = { deliveryToReceive = null },
            title = { Text(stringResource(R.string.receive_confirm_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.receive_confirm_desc)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.receiveInbound(target.id, null)
                        deliveryToReceive = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StockProGreenPrimary)
                ) {
                    Text(stringResource(R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { deliveryToReceive = null }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}
