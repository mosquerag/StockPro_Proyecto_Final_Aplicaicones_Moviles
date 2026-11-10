package com.example.ui.screens.products

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.R
import com.example.data.StockProAppContainer
import com.example.ui.components.StockProButton
import com.example.ui.components.StockProTextField
import com.example.ui.theme.StockProGreenPrimary
import com.example.ui.theme.StockProOutline
import com.example.ui.theme.StockProRedContainer
import com.example.ui.theme.StockProRedError
import com.example.ui.theme.StockProSurfaceVariant
import com.example.ui.theme.StockProTextSecondary
import com.example.ui.util.ErrorTranslator
import com.example.ui.viewmodel.NewEditProductViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEditProductScreen(
    productId: String? = null,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: NewEditProductViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val context = LocalContext.current
    val isEditMode = !productId.isNullOrBlank()

    val categoriesList by viewModel.categories.collectAsStateWithLifecycle()
    val existingProduct by viewModel.product.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var selectedCategoryName by remember { mutableStateOf("") }
    var costPrice by remember { mutableStateOf("0") }
    var salePrice by remember { mutableStateOf("0") }
    var initialStock by remember { mutableStateOf("0") }
    var minStock by remember { mutableStateOf("5") }
    var barcode by remember { mutableStateOf("") }
    var managesLots by remember { mutableStateOf(false) }
    var expirationDate by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var expandedCategory by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
        }
    }

    LaunchedEffect(productId) {
        if (!productId.isNullOrBlank()) {
            viewModel.loadProduct(productId)
        }
    }

    LaunchedEffect(existingProduct) {
        existingProduct?.let { p ->
            name = p.nombre
            selectedCategoryId = p.categoriaId ?: ""
            selectedCategoryName = p.categoriaNombre ?: ""
            costPrice = p.costo.toString()
            salePrice = p.precioVenta.toString()
            initialStock = p.stockActual.toString()
            minStock = p.stockMinimo.toString()
            barcode = p.codigoBarras.orEmpty()
            managesLots = p.manejaLotes
        }
    }

    LaunchedEffect(categoriesList) {
        if (selectedCategoryId.isBlank() && categoriesList.isNotEmpty()) {
            selectedCategoryId = categoriesList.first().id
            selectedCategoryName = categoriesList.first().nombre
        }
    }

    LaunchedEffect(saveState) {
        if (saveState is UiState.Success) {
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(if (isEditMode) R.string.title_edit_product else R.string.btn_new_product),
                        fontWeight = FontWeight.Bold
                    )
                },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            if (saveState is UiState.Error) {
                val errorCode = (saveState as UiState.Error).code
                Card(
                    colors = CardDefaults.cardColors(containerColor = StockProRedContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = ErrorTranslator.translate(context, errorCode),
                        color = StockProRedError,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // Selector de Imagen con Photo Picker de Android
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(StockProSurfaceVariant)
                    .border(1.dp, StockProOutline, RoundedCornerShape(16.dp))
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = StockProGreenPrimary,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Agregar imagen",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = StockProGreenPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            StockProTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.lbl_product_name),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selector de Categoría dinámica
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.lbl_category)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StockProGreenPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categoriesList.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            onClick = {
                                selectedCategoryId = cat.id
                                selectedCategoryName = cat.nombre
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fila: Precio de costo | Precio de venta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StockProTextField(
                    value = costPrice,
                    onValueChange = { costPrice = it },
                    label = stringResource(R.string.lbl_cost_price),
                    modifier = Modifier.weight(1f)
                )
                StockProTextField(
                    value = salePrice,
                    onValueChange = { salePrice = it },
                    label = stringResource(R.string.lbl_sale_price),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stock actual y Stock mínimo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StockProTextField(
                    value = initialStock,
                    onValueChange = { if (!isEditMode) initialStock = it },
                    label = stringResource(R.string.lbl_current_stock),
                    enabled = !isEditMode,
                    modifier = Modifier.weight(1f)
                )
                StockProTextField(
                    value = minStock,
                    onValueChange = { minStock = it },
                    label = stringResource(R.string.lbl_min_stock),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Switch para Lotes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.lbl_manages_lots),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                Switch(
                    checked = managesLots,
                    onCheckedChange = { managesLots = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = StockProGreenPrimary)
                )
            }

            if (managesLots && !isEditMode) {
                Spacer(modifier = Modifier.height(8.dp))
                StockProTextField(
                    value = expirationDate,
                    onValueChange = { expirationDate = it },
                    label = stringResource(R.string.lbl_expiration_date),
                    placeholder = "DD/MM/YYYY o YYYY-MM-DD",
                    leadingIcon = Icons.Default.CalendarToday
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (saveState is UiState.Loading) {
                CircularProgressIndicator(color = StockProGreenPrimary)
            } else {
                StockProButton(
                    text = stringResource(R.string.btn_save),
                    onClick = {
                        val cost = costPrice.toDoubleOrNull() ?: 0.0
                        val sale = salePrice.toDoubleOrNull() ?: 0.0
                        val minStk = minStock.toDoubleOrNull() ?: 5.0
                        val initStk = initialStock.toDoubleOrNull() ?: 0.0

                        viewModel.saveProduct(
                            productId = productId,
                            categoriaId = selectedCategoryId,
                            nombre = name,
                            codigoBarras = barcode,
                            costo = cost,
                            precioVenta = sale,
                            stockMinimo = minStk,
                            manejaLotes = managesLots,
                            cantidadInicial = if (!isEditMode && initStk > 0) initStk else null,
                            numeroLoteInicial = if (!isEditMode && managesLots && initStk > 0) "LOTE-INIT-1" else null,
                            fechaVencimientoInicial = if (!isEditMode && managesLots && expirationDate.isNotBlank()) expirationDate else null
                        )
                    },
                    testTag = "save_product_button"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
