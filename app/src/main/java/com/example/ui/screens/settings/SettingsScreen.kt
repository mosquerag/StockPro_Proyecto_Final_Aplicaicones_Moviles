package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.StockProAppContainer
import com.example.data.remote.dto.BusinessDetailsDto
import com.example.data.remote.dto.TarifaImpuestoDto
import com.example.ui.components.StockProButton
import com.example.ui.components.StockProTextField
import com.example.ui.theme.*
import com.example.ui.util.LocaleManager
import com.example.ui.viewmodel.SettingsViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToCategories: () -> Unit,
    onNavigateToInbound: () -> Unit,
    onLogout: () -> Unit,
    currentLanguage: String = "es",
    onLanguageChanged: (String) -> Unit = {},
    viewModel: SettingsViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showBusinessDialog by remember { mutableStateOf(false) }
    var showTaxRatesDialog by remember { mutableStateOf(false) }

    val businessState by viewModel.businessState.collectAsStateWithLifecycle()
    val taxRatesState by viewModel.taxRatesState.collectAsStateWithLifecycle()

    val businessName = (businessState as? UiState.Success)?.data?.nombre ?: viewModel.getBusinessName()
    val userEmail = viewModel.getUserEmail()
    val currency = (businessState as? UiState.Success)?.data?.monedaBase ?: viewModel.getBusinessCurrency()
    val country = (businessState as? UiState.Success)?.data?.pais ?: "CO"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings), fontWeight = FontWeight.Bold) },
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

            // Avatar de Tienda
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(StockProGreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = businessName,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = userEmail.ifBlank { "Moneda base: $currency ($country)" },
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Grupo de opciones de Negocio (Edición de Negocio y Tarifas 2026)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    SettingsOptionRow(
                        title = stringResource(R.string.opt_edit_business),
                        subtitle = "$country · $currency",
                        icon = Icons.Default.Business,
                        onClick = { showBusinessDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsOptionRow(
                        title = stringResource(R.string.lbl_tax_rates_title),
                        subtitle = stringResource(R.string.lbl_tax_rates_subtitle),
                        icon = Icons.Default.ReceiptLong,
                        onClick = { showTaxRatesDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grupo de opciones de Inventario (Categorías y Entradas)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    SettingsOptionRow(
                        title = stringResource(R.string.title_categories),
                        icon = Icons.Default.Category,
                        onClick = onNavigateToCategories
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsOptionRow(
                        title = stringResource(R.string.title_inbound_deliveries),
                        icon = Icons.Default.LocalShipping,
                        onClick = onNavigateToInbound
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grupo Preferencias (Idioma, Notificaciones)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    SettingsOptionRow(
                        title = stringResource(R.string.opt_language),
                        subtitle = if (currentLanguage == "es") stringResource(R.string.lang_spanish) else stringResource(R.string.lang_english),
                        icon = Icons.Default.Translate,
                        onClick = { showLanguageDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = StockProGreenPrimary)
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(text = stringResource(R.string.opt_notifications), style = MaterialTheme.typography.bodyLarge)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = StockProGreenPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Opción Cerrar Sesión (Limpia tokens y Room)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogoutDialog = true }
                    .testTag("logout_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StockProRedContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = StockProRedError)
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = stringResource(R.string.btn_logout),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = StockProRedError
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Modal para Ver y Editar Negocio (GET / PUT /api/v1/business)
    if (showBusinessDialog) {
        val currentBusiness = (businessState as? UiState.Success)?.data
        var editNombre by remember(currentBusiness) { mutableStateOf(currentBusiness?.nombre ?: businessName) }
        var editPais by remember(currentBusiness) { mutableStateOf(currentBusiness?.pais ?: "CO") }
        var editEstadoRegion by remember(currentBusiness) { mutableStateOf(currentBusiness?.estadoRegion ?: "") }
        var editMoneda by remember(currentBusiness) { mutableStateOf(currentBusiness?.monedaBase ?: "COP") }

        AlertDialog(
            onDismissRequest = { showBusinessDialog = false },
            title = { Text(stringResource(R.string.opt_edit_business), fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StockProTextField(
                        value = editNombre,
                        onValueChange = { editNombre = it },
                        label = stringResource(R.string.lbl_business_name),
                        modifier = Modifier.testTag("business_name_input")
                    )

                    // Selector de País (Colombia, USA y Zona Euro: España, Italia, Alemania, Francia, Portugal)
                    Text(stringResource(R.string.lbl_country), style = MaterialTheme.typography.labelMedium)
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val countries = listOf(
                            "CO" to "Colombia (COP)",
                            "US" to "USA (USD)",
                            "ES" to "España (EUR)",
                            "IT" to "Italia (EUR)",
                            "DE" to "Alemania (EUR)",
                            "FR" to "Francia (EUR)",
                            "PT" to "Portugal (EUR)"
                        )
                        items(countries) { (code, label) ->
                            FilterChip(
                                selected = editPais == code,
                                onClick = {
                                    editPais = code
                                    when (code) {
                                        "CO" -> editMoneda = "COP"
                                        "US" -> editMoneda = "USD"
                                        else -> editMoneda = "EUR" // Países de la zona euro usan EUR
                                    }
                                },
                                label = { Text(label) }
                            )
                        }
                    }

                    if (editPais == "US") {
                        StockProTextField(
                            value = editEstadoRegion,
                            onValueChange = { editEstadoRegion = it },
                            label = stringResource(R.string.lbl_state_us),
                            placeholder = "Ej. CA, NY, TX",
                            modifier = Modifier.testTag("state_region_input")
                        )
                    }

                    // Selector de Moneda (COP, USD, EUR)
                    Text(stringResource(R.string.lbl_currency), style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("COP", "USD", "EUR").forEach { curr ->
                            FilterChip(
                                selected = editMoneda == curr,
                                onClick = { editMoneda = curr },
                                label = { Text(curr) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                StockProButton(
                    text = stringResource(R.string.btn_save),
                    onClick = {
                        viewModel.updateBusiness(
                            nombre = editNombre,
                            pais = editPais,
                            estadoRegion = if (editPais == "US") editEstadoRegion else null,
                            monedaBase = editMoneda,
                            onSuccess = { showBusinessDialog = false }
                        )
                    },
                    modifier = Modifier.testTag("save_business_button")
                )
            },
            dismissButton = {
                TextButton(onClick = { showBusinessDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    // Modal para consultar Tarifas de Impuestos 2026 (GET /api/v1/tax-rates)
    if (showTaxRatesDialog) {
        AlertDialog(
            onDismissRequest = { showTaxRatesDialog = false },
            title = { Text(stringResource(R.string.lbl_tax_rates_title), fontWeight = FontWeight.Bold) },
            text = {
                when (val state = taxRatesState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = StockProGreenPrimary)
                        }
                    }
                    is UiState.Success -> {
                        val rates = state.data
                        if (rates.isEmpty()) {
                            Text(stringResource(R.string.lbl_no_tax_rates))
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(rates) { rate ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${rate.tipoImpuesto} (${rate.categoriaFiscal})",
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                rate.descripcion?.let {
                                                    Text(it, style = MaterialTheme.typography.bodySmall)
                                                }
                                            }
                                            Text(
                                                text = "${rate.porcentaje}%",
                                                fontWeight = FontWeight.Bold,
                                                color = StockProGreenPrimary,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        Text(
                            text = state.message ?: state.code,
                            color = StockProRedError
                        )
                    }
                    else -> {}
                }
            },
            confirmButton = {
                TextButton(onClick = { showTaxRatesDialog = false }) {
                    Text(stringResource(R.string.btn_close))
                }
            }
        )
    }

    // Modal de selección de idioma con aplicación inmediata
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.opt_language), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                LocaleManager.saveLanguage(context, "es")
                                onLanguageChanged("es")
                                showLanguageDialog = false
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == "es",
                            onClick = {
                                LocaleManager.saveLanguage(context, "es")
                                onLanguageChanged("es")
                                showLanguageDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.lang_spanish), fontWeight = FontWeight.Medium)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                LocaleManager.saveLanguage(context, "en")
                                onLanguageChanged("en")
                                showLanguageDialog = false
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == "en",
                            onClick = {
                                LocaleManager.saveLanguage(context, "en")
                                onLanguageChanged("en")
                                showLanguageDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.lang_english), fontWeight = FontWeight.Medium)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    // Diálogo de Confirmación de Cierre de Sesión (Limpia sesión y Room)
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = StockProRedError) },
            title = { Text(stringResource(R.string.logout_confirm_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.logout_confirm_desc)) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout { onLogout() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StockProRedError),
                    modifier = Modifier.testTag("confirm_logout_button")
                ) {
                    Text(stringResource(R.string.btn_logout), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@Composable
private fun SettingsOptionRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = StockProGreenPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}
