package com.example.ui.screens.movements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.StockProAppContainer
import com.example.ui.components.StockProButton
import com.example.ui.components.StockProTextField
import com.example.ui.theme.StockProGreenPrimary
import com.example.ui.theme.StockProRedContainer
import com.example.ui.theme.StockProRedError
import com.example.ui.util.ErrorTranslator
import com.example.ui.viewmodel.MovementsViewModel
import com.example.ui.viewmodel.ProductsViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordMovementScreen(
    initialType: String = "ENTRADA",
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    movementsViewModel: MovementsViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    ),
    productsViewModel: ProductsViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val context = LocalContext.current
    val productsState by productsViewModel.productsState.collectAsStateWithLifecycle()
    val recordState by movementsViewModel.recordState.collectAsStateWithLifecycle()

    val products = (productsState as? UiState.Success)?.data ?: emptyList()
    var selectedProductId by remember { mutableStateOf("") }
    var selectedProductName by remember { mutableStateOf("") }
    var expandedProduct by remember { mutableStateOf(false) }

    var movementType by remember { mutableStateOf(initialType) }
    var expandedType by remember { mutableStateOf(false) }
    val types = listOf("ENTRADA", "PERDIDA", "DANADO", "AJUSTE_POSITIVO", "AJUSTE_NEGATIVO")

    var quantity by remember { mutableStateOf("1") }
    var reason by remember { mutableStateOf("") }

    LaunchedEffect(products) {
        if (selectedProductId.isBlank() && products.isNotEmpty()) {
            selectedProductId = products.first().id
            selectedProductName = products.first().nombre
        }
    }

    LaunchedEffect(recordState) {
        if (recordState is UiState.Success) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_record_movement), fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            if (recordState is UiState.Error) {
                val errorCode = (recordState as UiState.Error).code
                Card(
                    colors = CardDefaults.cardColors(containerColor = StockProRedContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = ErrorTranslator.translate(context, errorCode),
                        color = StockProRedError,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // Selector de Producto
            ExposedDropdownMenuBox(
                expanded = expandedProduct,
                onExpandedChange = { expandedProduct = !expandedProduct },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedProductName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.title_products)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProduct) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = StockProGreenPrimary),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedProduct,
                    onDismissRequest = { expandedProduct = false }
                ) {
                    products.forEach { prod ->
                        DropdownMenuItem(
                            text = { Text("${prod.nombre} (Stock: ${prod.stockActual.toInt()})") },
                            onClick = {
                                selectedProductId = prod.id
                                selectedProductName = prod.nombre
                                expandedProduct = false
                            }
                        )
                    }
                }
            }

            // Selector Tipo de Movimiento
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = !expandedType },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = movementType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.lbl_movement_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = StockProGreenPrimary),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    types.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t) },
                            onClick = {
                                movementType = t
                                expandedType = false
                            }
                        )
                    }
                }
            }

            StockProTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = stringResource(R.string.lbl_quantity)
            )

            StockProTextField(
                value = reason,
                onValueChange = { reason = it },
                label = stringResource(R.string.lbl_reason),
                singleLine = false,
                modifier = Modifier.heightIn(min = 100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (recordState is UiState.Loading) {
                CircularProgressIndicator(color = StockProGreenPrimary)
            } else {
                StockProButton(
                    text = stringResource(R.string.btn_save),
                    onClick = {
                        val qty = quantity.toDoubleOrNull() ?: 1.0
                        val finalReason = if (reason.isBlank()) "Movimiento de $movementType" else reason
                        movementsViewModel.recordMovement(
                            productoId = selectedProductId,
                            tipo = movementType,
                            cantidad = qty,
                            motivo = finalReason
                        )
                    },
                    testTag = "save_movement_button"
                )
            }
        }
    }
}
