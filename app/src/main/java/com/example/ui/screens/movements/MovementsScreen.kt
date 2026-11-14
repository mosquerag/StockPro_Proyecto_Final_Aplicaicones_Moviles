package com.example.ui.screens.movements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.*
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.MovementsViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementsScreen(
    onNavigateToRegisterSale: () -> Unit,
    onNavigateToRecordMovement: (String) -> Unit,
    currencyCode: String = "COP",
    viewModel: MovementsViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val movementsState by viewModel.movementsState.collectAsStateWithLifecycle()
    val movements = (movementsState as? UiState.Success)?.data ?: emptyList()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.loadMovements()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_movements), fontWeight = FontWeight.Bold) },
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
            // 4 Tarjetas de acción rápida
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MovementActionCard(
                        title = stringResource(R.string.move_sale),
                        subtitle = stringResource(R.string.move_sale_desc),
                        icon = Icons.Default.ShoppingCart,
                        iconColor = StockProGreenPrimary,
                        containerColor = StockProGreenContainer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToRegisterSale
                    )
                    MovementActionCard(
                        title = stringResource(R.string.move_inbound),
                        subtitle = stringResource(R.string.move_inbound_desc),
                        icon = Icons.Default.AddBusiness,
                        iconColor = StockProBlue,
                        containerColor = StockProBlueContainer,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToRecordMovement("ENTRADA") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MovementActionCard(
                        title = stringResource(R.string.move_loss),
                        subtitle = stringResource(R.string.move_loss_desc),
                        icon = Icons.Default.ReportProblem,
                        iconColor = StockProRedError,
                        containerColor = StockProRedContainer,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToRecordMovement("PERDIDA") }
                    )
                    MovementActionCard(
                        title = stringResource(R.string.move_damaged),
                        subtitle = stringResource(R.string.move_damaged_desc),
                        icon = Icons.Default.BrokenImage,
                        iconColor = StockProOrange,
                        containerColor = StockProOrangeContainer,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToRecordMovement("DANADO") }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Historial de movimientos reales
            item {
                Text(
                    text = stringResource(R.string.recent_history),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (movementsState is UiState.Loading && movements.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = StockProGreenPrimary)
                    }
                }
            } else if (movements.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = stringResource(R.string.empty_movements_title),
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(movements) { mov ->
                    val isPositive = mov.tipoMovimiento == "ENTRADA" || mov.tipoMovimiento == "AJUSTE_POSITIVO" || mov.tipoMovimiento == "DEVOLUCION"
                    val isSale = mov.tipoMovimiento == "VENTA"
                    val badgeColor = when (mov.tipoMovimiento) {
                        "VENTA" -> StockProGreenPrimary
                        "ENTRADA" -> StockProBlue
                        "PERDIDA" -> StockProRedError
                        else -> StockProOrange
                    }
                    val badgeBg = when (mov.tipoMovimiento) {
                        "VENTA" -> StockProGreenContainer
                        "ENTRADA" -> StockProBlueContainer
                        "PERDIDA" -> StockProRedContainer
                        else -> StockProOrangeContainer
                    }

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
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(badgeBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (mov.tipoMovimiento) {
                                        "VENTA" -> Icons.Default.ShoppingBag
                                        "ENTRADA" -> Icons.Default.ArrowDownward
                                        "PERDIDA" -> Icons.Default.DeleteOutline
                                        else -> Icons.Default.WarningAmber
                                    },
                                    contentDescription = null,
                                    tint = badgeColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${mov.tipoMovimiento} - ${mov.productoNombre ?: "Producto"}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                )
                                Text(
                                    text = mov.motivo,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                mov.usuarioNombre?.let { user ->
                                    Text(
                                        text = user,
                                        style = MaterialTheme.typography.labelSmall.copy(color = StockProTextMuted)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${if (isPositive) "+" else "-"}${mov.cantidad.toInt()} un.",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPositive || isSale) StockProGreenDark else StockProRedError
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun MovementActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("action_card_$title"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp),
                maxLines = 1
            )
        }
    }
}
