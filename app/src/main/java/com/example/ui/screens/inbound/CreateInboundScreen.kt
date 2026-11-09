package com.example.ui.screens.inbound

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
import com.example.data.remote.dto.CreateDetalleEntradaRequestDto
import com.example.ui.components.StockProButton
import com.example.ui.components.StockProTextField
import com.example.ui.theme.StockProGreenPrimary
import com.example.ui.theme.StockProRedContainer
import com.example.ui.theme.StockProRedError
import com.example.ui.util.ErrorTranslator
import com.example.ui.viewmodel.InboundViewModel
import com.example.ui.viewmodel.ProductsViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInboundScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    inboundViewModel: InboundViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    ),
    productsViewModel: ProductsViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val context = LocalContext.current
    val productsState by productsViewModel.productsState.collectAsStateWithLifecycle()
    val createState by inboundViewModel.createState.collectAsStateWithLifecycle()

    val products = (productsState as? UiState.Success)?.data ?: emptyList()

    var originReference by remember { mutableStateOf("") }
    var expectedDateOption by remember { mutableStateOf("Hoy") }
    var expandedDate by remember { mutableStateOf(false) }

    var selectedProductId by remember { mutableStateOf("") }
    var selectedProductName by remember { mutableStateOf("") }
    var expandedProduct by remember { mutableStateOf(false) }
    var expectedQuantity by remember { mutableStateOf("20") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(products) {
        if (selectedProductId.isBlank() && products.isNotEmpty()) {
            selectedProductId = products.first().id
            selectedProductName = products.first().nombre
        }
    }

    LaunchedEffect(createState) {
        if (createState is UiState.Success) {
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.btn_new_inbound), fontWeight = FontWeight.Bold) },
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

            if (createState is UiState.Error) {
                val errorCode = (createState as UiState.Error).code
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

            // Origen / Referencia (texto libre opcional, sin módulo de proveedores)
            StockProTextField(
                value = originReference,
                onValueChange = { originReference = it },
                label = stringResource(R.string.lbl_origin_reference),
                placeholder = stringResource(R.string.hint_origin_reference)
            )

            // Selector Fecha Prevista (Hoy o Mañana)
            ExposedDropdownMenuBox(
                expanded = expandedDate,
                onExpandedChange = { expandedDate = !expandedDate },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = expectedDateOption,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.lbl_expected_date)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDate) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = StockProGreenPrimary),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedDate,
                    onDismissRequest = { expandedDate = false }
                ) {
                    listOf("Hoy", "Mañana").forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = {
                                expectedDateOption = opt
                                expandedDate = false
                            }
                        )
                    }
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
                            text = { Text(prod.nombre) },
                            onClick = {
                                selectedProductId = prod.id
                                selectedProductName = prod.nombre
                                expandedProduct = false
                            }
                        )
                    }
                }
            }

            StockProTextField(
                value = expectedQuantity,
                onValueChange = { expectedQuantity = it },
                label = stringResource(R.string.lbl_quantity)
            )

            StockProTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notas u observaciones",
                singleLine = false,
                modifier = Modifier.heightIn(min = 90.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (createState is UiState.Loading) {
                CircularProgressIndicator(color = StockProGreenPrimary)
            } else {
                StockProButton(
                    text = stringResource(R.string.btn_save),
                    onClick = {
                        val date = if (expectedDateOption == "Mañana") {
                            LocalDate.now().plusDays(1).toString()
                        } else {
                            LocalDate.now().toString()
                        }
                        val qty = expectedQuantity.toDoubleOrNull() ?: 1.0
                        inboundViewModel.createInbound(
                            fechaPrevista = date,
                            origen = originReference,
                            notas = notes,
                            items = listOf(
                                CreateDetalleEntradaRequestDto(
                                    productoId = selectedProductId,
                                    cantidadEsperada = qty
                                )
                            )
                        )
                    },
                    testTag = "save_inbound_button"
                )
            }
        }
    }
}
