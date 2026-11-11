package com.example.ui.screens.sales

import android.util.Log
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
import androidx.compose.ui.unit.sp
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
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val saleState by viewModel.saleState.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("EFECTIVO") }
    var expandedPaymentMenu by remember { mutableStateOf(false) }

    val paymentMethods = listOf("EFECTIVO", "TARJETA", "TRANSFERENCIA", "MIXTO")

    BackHandler {
        onNavigateBack()
    }

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    LaunchedEffect(saleState) {
        if (saleState is UiState.Success) {
            val created = (saleState as UiState.Success).data
            Log.d("STOCKPRO_DEBUG", "Venta creada exitosamente con ID: ${created.id}")
            onSaleCompleted(created.id)
        }
    }

    val filteredProducts = remember(searchQuery, products) {
        products.filter { it.nombre.contains(searchQuery, ignoreCase = true) }
    }

    val productMap = remember(products) {
        products.associateBy { it.id }
    }

    val totalSelectedUnits = remember(cart) {
        cart.values.sum().toInt()
    }

    val totalAmount = remember(cart, productMap) {
        var sum = BigDecimal.ZERO
        cart.forEach { (prodId, qty) ->
            val p = productMap[prodId]
            if (p != null) {
                sum = sum.add(BigDecimal.valueOf(p.precioVenta).multiply(BigDecimal.valueOf(qty)))
            }
        }
        sum
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
                        val errorText = ErrorTranslator.translate(context, errorCode)
                        Log.e("STOCKPRO_DEBUG", "Error al procesar venta: $errorCode - $errorText")
                        Card(
                            colors = CardDefaults.cardColors(containerColor = StockProRedContainer),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Text(
                                text = errorText,
                                color = StockProRedError,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Resumen de Venta Multiproducto
                    Text(
                        text = "Resumen de venta",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$totalSelectedUnits unidades seleccionadas",
                            style = MaterialTheme.typography.bodyMedium.copy(color = StockProTextSecondary)
                        )
                        Text(
                            text = Formatters.formatCurrency(totalAmount, currencyCode),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Selector Método de Pago
                    ExposedDropdownMenuBox(
                        expanded = expandedPaymentMenu,
                        onExpandedChange = { expandedPaymentMenu = !expandedPaymentMenu },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedPaymentMethod,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.lbl_payment_method)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPaymentMenu) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = StockProGreenPrimary),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedPaymentMenu,
                            onDismissRequest = { expandedPaymentMenu = false }
                        ) {
                            paymentMethods.forEach { method ->
                                DropdownMenuItem(
                                    text = { Text(method) },
                                    onClick = {
                                        selectedPaymentMethod = method
                                        expandedPaymentMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

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
                            enabled = totalSelectedUnits > 0,
                            onClick = {
                                viewModel.submitSale(currencyCode, selectedPaymentMethod)
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

            // Lista Multiproducto de Carrito con Stepper por fila
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredProducts) { product ->
                    val isOutOfStock = product.stockActual <= 0
                    val currentQty = cart[product.id]?.toInt() ?: 0
                    val maxAvailable = product.stockActual.toInt()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isOutOfStock) 0.5f else 1.0f)
                            .then(
                                if (currentQty > 0) Modifier.border(2.dp, StockProGreenPrimary, RoundedCornerShape(14.dp))
                                else Modifier
                            ),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentQty > 0) StockProGreenContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
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
                                Text(
                                    text = if (isOutOfStock) "AGOTADO" else "Stock: $maxAvailable",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isOutOfStock) StockProRedError else StockProTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }

                            // Stepper de Cantidad por Producto ( -  qty  + )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledIconButton(
                                    onClick = {
                                        if (currentQty > 0) {
                                            viewModel.updateCart(product.id, (currentQty - 1).toDouble())
                                        }
                                    },
                                    enabled = !isOutOfStock && currentQty > 0,
                                    shape = CircleShape,
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = StockProSurfaceVariant),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
                                }

                                Text(
                                    text = currentQty.toString(),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                FilledIconButton(
                                    onClick = {
                                        if (currentQty < maxAvailable) {
                                            viewModel.updateCart(product.id, (currentQty + 1).toDouble())
                                        }
                                    },
                                    enabled = !isOutOfStock && currentQty < maxAvailable,
                                    shape = CircleShape,
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = StockProSurfaceVariant),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
