package com.example.ui.screens.sales

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.StockProAppContainer
import com.example.ui.components.StockProButton
import com.example.ui.components.StockProTextField
import com.example.ui.theme.*
import com.example.ui.util.ErrorTranslator
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.RegisterSaleViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterSaleScreen(
    onNavigateBack: () -> Unit,
    onSaleCompleted: (String) -> Unit,
    currencyCode: String = "COP",
    viewModel: RegisterSaleViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsStateWithLifecycle()
    val saleState by viewModel.saleState.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedProductId by remember { mutableStateOf<String?>(null) }
    var quantity by remember { mutableStateOf(1) }
    var selectedPaymentMethod by remember { mutableStateOf("EFECTIVO") }

    BackHandler {
        onNavigateBack()
    }

    LaunchedEffect(products) {
        if (selectedProductId == null) {
            val available = products.firstOrNull { it.stockActual > 0 }
            if (available != null) {
                selectedProductId = available.id
            }
        }
    }

    LaunchedEffect(saleState) {
        if (saleState is UiState.Success) {
            val created = (saleState as UiState.Success).data
            onSaleCompleted(created.id)
        }
    }

    val filteredProducts = remember(searchQuery, products) {
        products.filter { it.nombre.contains(searchQuery, ignoreCase = true) }
    }

    val selectedProduct = remember(selectedProductId, products) {
        products.find { it.id == selectedProductId }
    }

    val maxStockAvailable = (selectedProduct?.stockActual ?: 0.0).toInt()

    val totalAmount = remember(selectedProduct, quantity) {
        if (selectedProduct != null && selectedProduct.stockActual > 0) {
            BigDecimal.valueOf(selectedProduct.precioVenta).multiply(BigDecimal(quantity))
        } else BigDecimal.ZERO
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_register_sale), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    if (saleState is UiState.Error) {
                        val errorCode = (saleState as UiState.Error).code
                        Card(
                            colors = CardDefaults.cardColors(containerColor = StockProRedContainer),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Text(
                                text = ErrorTranslator.translate(context, errorCode),
                                color = StockProRedError,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.lbl_total, Formatters.formatCurrency(totalAmount, currencyCode)),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = StockProGreenDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (saleState is UiState.Loading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = StockProGreenPrimary)
                        }
                    } else {
                        StockProButton(
                            text = stringResource(R.string.btn_complete_sale),
                            enabled = selectedProduct != null && selectedProduct.stockActual > 0 && maxStockAvailable > 0,
                            onClick = {
                                if (selectedProductId != null && maxStockAvailable > 0) {
                                    val finalQty = quantity.coerceAtMost(maxStockAvailable)
                                    viewModel.updateCart(selectedProductId!!, finalQty.toDouble())
                                    viewModel.submitSale(currencyCode, selectedPaymentMethod)
                                }
                            },
                            testTag = "confirm_sale_button"
                        )
                    }
                }
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
            StockProTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = stringResource(R.string.search_product_hint),
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de productos seleccionables
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredProducts) { product ->
                    val isOutOfStock = product.stockActual <= 0
                    val isSelected = product.id == selectedProductId && !isOutOfStock

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isOutOfStock) 0.5f else 1.0f)
                            .clickable(enabled = !isOutOfStock) {
                                selectedProductId = product.id
                                quantity = 1
                            }
                            .then(
                                if (isSelected) Modifier.border(2.dp, StockProGreenPrimary, RoundedCornerShape(14.dp))
                                else Modifier
                            ),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) StockProGreenContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.nombre,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = Formatters.formatCurrency(BigDecimal.valueOf(product.precioVenta), currencyCode),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = StockProGreenDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Surface(
                                color = if (isOutOfStock) StockProRedContainer else StockProSurfaceVariant,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isOutOfStock) "AGOTADO" else "Stock: ${product.stockActual.toInt()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isOutOfStock) StockProRedError else StockProTextSecondary,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector de Cantidad con Stepper
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.lbl_quantity),
                        style = MaterialTheme.typography.labelMedium.copy(color = StockProTextSecondary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilledIconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            enabled = selectedProduct != null && selectedProduct.stockActual > 0 && quantity > 1,
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = StockProSurfaceVariant),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        }

                        Text(
                            text = if (selectedProduct != null && selectedProduct.stockActual > 0) quantity.toString() else "0",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )

                        FilledIconButton(
                            onClick = { if (quantity < maxStockAvailable) quantity++ },
                            enabled = selectedProduct != null && selectedProduct.stockActual > 0 && quantity < maxStockAvailable,
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = StockProSurfaceVariant),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
