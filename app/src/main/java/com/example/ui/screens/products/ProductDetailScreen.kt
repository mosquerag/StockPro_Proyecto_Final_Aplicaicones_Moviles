package com.example.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.StockProAppContainer
import com.example.data.remote.dto.LoteDto
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.ProductDetailViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    currencyCode: String = "COP",
    viewModel: ProductDetailViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val productState by viewModel.productState.collectAsStateWithLifecycle()
    val lotsState by viewModel.lotsState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    var lotToWriteOff by remember { mutableStateOf<LoteDto?>(null) }

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    val product = (productState as? UiState.Success)?.data
    val lots = (lotsState as? UiState.Success)?.data ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_product_detail), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back))
                    }
                },
                actions = {
                    if (product != null) {
                        IconButton(onClick = { onNavigateToEdit(product.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.title_edit_product), tint = StockProGreenPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        if (productState is UiState.Loading && product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = StockProGreenPrimary)
            }
        } else if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.empty_products_title))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
            ) {
                // Tarjeta Cabecera del Producto
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val imgUrl = product.imagenUrl
                        val thumbModifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(14.dp))

                        if (!imgUrl.isNullOrBlank()) {
                            val imageModel = if (imgUrl.startsWith("/") && !imgUrl.startsWith("http")) {
                                java.io.File(imgUrl)
                            } else {
                                imgUrl
                            }
                            SubcomposeAsyncImage(
                                model = imageModel,
                                contentDescription = product.nombre,
                                modifier = thumbModifier,
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box(
                                        modifier = thumbModifier.background(StockProGreenContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = StockProGreenPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                },
                                error = {
                                    Box(
                                        modifier = thumbModifier.background(StockProGreenContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Inventory, contentDescription = null, tint = StockProGreenPrimary, modifier = Modifier.size(32.dp))
                                    }
                                }
                            )
                        } else {
                            Box(
                                modifier = thumbModifier.background(StockProGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Inventory, contentDescription = null, tint = StockProGreenPrimary, modifier = Modifier.size(32.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.nombre,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = product.categoriaNombre ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = Formatters.formatCurrency(BigDecimal.valueOf(product.precioVenta), currencyCode),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = StockProGreenDark
                                )
                            )
                            Surface(
                                color = if (product.stockBajo) StockProRedContainer else StockProGreenContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.stock_units_format, product.stockActual.toInt().toString()),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (product.stockBajo) StockProRedError else StockProGreenDark
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Tabs: Información vs Lotes
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = StockProGreenPrimary,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(stringResource(R.string.tab_info), fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(stringResource(R.string.tab_lots), fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // Información general
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            DetailItemRow(stringResource(R.string.lbl_cost_price), Formatters.formatCurrency(BigDecimal.valueOf(product.costo), currencyCode))
                            DetailItemRow(stringResource(R.string.lbl_sale_price), Formatters.formatCurrency(BigDecimal.valueOf(product.precioVenta), currencyCode))
                            DetailItemRow(stringResource(R.string.lbl_current_stock), stringResource(R.string.stock_units_format, product.stockActual.toString()))
                            DetailItemRow(stringResource(R.string.lbl_min_stock), stringResource(R.string.stock_units_format, product.stockMinimo.toString()))
                            DetailItemRow(stringResource(R.string.lbl_fiscal_category), product.categoriaFiscal)
                            DetailItemRow(stringResource(R.string.lbl_manages_lots), if (product.manejaLotes) "Sí" else "No")
                        }
                    }
                } else {
                    // Listado de Lotes y Vencimientos reales
                    if (lots.isEmpty()) {
                        EmptyStateView(
                            title = stringResource(R.string.empty_lots_title),
                            description = stringResource(R.string.empty_lots_subtitle),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(lots) { lot ->
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
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = stringResource(R.string.lot_number_label, lot.numeroLote),
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = stringResource(R.string.lot_available_label, lot.cantidadDisponible.toString()),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            lot.fechaVencimiento?.let { exp ->
                                                Text(
                                                    text = stringResource(R.string.lot_expires_label, exp),
                                                    style = MaterialTheme.typography.bodySmall.copy(color = StockProYellowWarning, fontWeight = FontWeight.SemiBold)
                                                )
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = { lotToWriteOff = lot },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StockProRedError),
                                            modifier = Modifier.testTag("write_off_lot_button")
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
    }

    // Modal de confirmación para dar de baja el lote por pérdida/vencimiento
    if (lotToWriteOff != null) {
        val targetLot = lotToWriteOff!!
        AlertDialog(
            onDismissRequest = { lotToWriteOff = null },
            title = { Text(stringResource(R.string.write_off_confirm_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.write_off_confirm_desc)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.darDeBajaLote(productId, targetLot.id, "Baja de lote por vencimiento o deterioro")
                        lotToWriteOff = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StockProRedError)
                ) {
                    Text(stringResource(R.string.btn_confirm), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { lotToWriteOff = null }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@Composable
private fun DetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
    }
}
