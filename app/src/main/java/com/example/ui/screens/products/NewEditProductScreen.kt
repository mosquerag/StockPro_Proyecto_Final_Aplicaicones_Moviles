package com.example.ui.screens.products

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material.icons.filled.ArrowDropDown
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

private fun Context.findActivity(): ComponentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    return null
}

fun saveImageLocally(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "prod_img_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

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
    val activity = context.findActivity()

    if (activity != null) {
        CompositionLocalProvider(LocalActivityResultRegistryOwner provides activity) {
            NewEditProductScreenContent(
                productId = productId,
                onNavigateBack = onNavigateBack,
                onSaveSuccess = onSaveSuccess,
                viewModel = viewModel
            )
        }
    } else {
        NewEditProductScreenContent(
            productId = productId,
            onNavigateBack = onNavigateBack,
            onSaveSuccess = onSaveSuccess,
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewEditProductScreenContent(
    productId: String? = null,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: NewEditProductViewModel
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
    var localImagePath by remember { mutableStateOf<String?>(null) }

    var expandedCategory by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val savedPath = saveImageLocally(context, uri)
            if (savedPath != null) {
                localImagePath = savedPath
                imageUri = Uri.fromFile(File(savedPath))
                Log.d("STOCKPRO_DEBUG", "Imagen copiada localmente a: $savedPath")
            } else {
                imageUri = uri
                localImagePath = uri.toString()
            }
        }
    }

    LaunchedEffect(productId) {
        if (!productId.isNullOrBlank()) {
            Log.d("STOCKPRO_DEBUG", "Cargando producto para edición con ID: $productId")
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
            managesLots = p.manejaLotes || !p.fechaVencimiento.isNullOrBlank()
            if (!p.fechaVencimiento.isNullOrBlank()) {
                expirationDate = p.fechaVencimiento
            }
            if (!p.imagenUrl.isNullOrBlank()) {
                localImagePath = p.imagenUrl
                val imgFile = File(p.imagenUrl)
                imageUri = if (imgFile.exists()) Uri.fromFile(imgFile) else Uri.parse(p.imagenUrl)
            }
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
            Log.d("STOCKPRO_DEBUG", "Producto guardado exitosamente")
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
                val errorMsg = ErrorTranslator.translate(context, errorCode)
                Log.e("STOCKPRO_DEBUG", "Error al guardar producto: $errorCode - $errorMsg")
                Card(
                    colors = CardDefaults.cardColors(containerColor = StockProRedContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = errorMsg,
                        color = StockProRedError,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // Selector de Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(StockProSurfaceVariant)
                    .border(1.dp, StockProOutline, RoundedCornerShape(16.dp))
                    .clickable {
                        photoPickerLauncher.launch("image/*")
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

            // Selector de Categoría Robusto
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategoryName.ifBlank { "Seleccionar categoría" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.lbl_category)) },
                    trailingIcon = {
                        IconButton(onClick = { expandedCategory = !expandedCategory }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = StockProGreenPrimary)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StockProGreenPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedCategory = !expandedCategory }
                )

                DropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    if (categoriesList.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Cargando categorías...") },
                            onClick = { expandedCategory = false }
                        )
                    } else {
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
                    onValueChange = { initialStock = it },
                    label = stringResource(R.string.lbl_current_stock),
                    enabled = true,
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

            var showDatePickerDialog by remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState()

            if (showDatePickerDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDatePickerDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedMs = datePickerState.selectedDateMillis
                                if (selectedMs != null) {
                                    val instant = java.time.Instant.ofEpochMilli(selectedMs)
                                    val localDate = instant.atZone(java.time.ZoneId.of("UTC")).toLocalDate()
                                    expirationDate = String.format(
                                        "%04d-%02d-%02d",
                                        localDate.year,
                                        localDate.monthValue,
                                        localDate.dayOfMonth
                                    )
                                }
                                showDatePickerDialog = false
                            }
                        ) {
                            Text("Aceptar", color = StockProGreenPrimary, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePickerDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (managesLots) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = expirationDate,
                    onValueChange = { expirationDate = it },
                    label = { Text(stringResource(R.string.lbl_expiration_date)) },
                    placeholder = { Text("YYYY-MM-DD") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePickerDialog = true }) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Seleccionar fecha",
                                tint = StockProGreenPrimary
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StockProGreenPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePickerDialog = true }
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
                            cantidadInicial = initStk,
                            numeroLoteInicial = if (managesLots && initStk > 0) "LOTE-INIT-1" else null,
                            fechaVencimientoInicial = if (managesLots && expirationDate.isNotBlank()) expirationDate else null,
                            imagenUrl = localImagePath ?: imageUri?.toString()
                        )
                    },
                    testTag = "save_product_button"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
