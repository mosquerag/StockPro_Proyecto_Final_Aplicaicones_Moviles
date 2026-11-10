package com.example.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.StockProAppContainer
import com.example.ui.components.StockProButton
import com.example.ui.components.StockProTextField
import com.example.ui.theme.StockProGreenContainer
import com.example.ui.theme.StockProGreenDark
import com.example.ui.theme.StockProGreenPrimary
import com.example.ui.theme.StockProRedContainer
import com.example.ui.theme.StockProRedError
import com.example.ui.util.ErrorTranslator
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val context = LocalContext.current
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()

    var ownerName by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf("") }
    var selectedBusinessType by remember { mutableStateOf("TIENDA") }
    var selectedCountry by remember { mutableStateOf("CO") }
    var selectedCurrency by remember { mutableStateOf("COP") }
    var stateUs by remember { mutableStateOf("") }

    val businessTypes = listOf("TIENDA", "SUPERMERCADO", "RESTAURANTE", "ROPA", "DROGUERIA", "TECNOLOGIA", "OTRO")
    var expandedType by remember { mutableStateOf(false) }

    LaunchedEffect(registerState) {
        if (registerState is UiState.Success) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_register), fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.register_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (registerState is UiState.Success) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = StockProGreenContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StockProGreenDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cuenta creada con éxito. Iniciando sesión...",
                            color = StockProGreenDark,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }

            if (registerState is UiState.Error) {
                val errorCode = (registerState as UiState.Error).code
                Card(
                    colors = CardDefaults.cardColors(containerColor = StockProRedContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = ErrorTranslator.translate(context, errorCode),
                        color = StockProRedError,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            StockProTextField(
                value = ownerName,
                onValueChange = { ownerName = it },
                label = stringResource(R.string.lbl_owner_name),
                placeholder = stringResource(R.string.hint_owner_name),
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(14.dp))

            StockProTextField(
                value = businessName,
                onValueChange = { businessName = it },
                label = stringResource(R.string.lbl_business_name),
                placeholder = stringResource(R.string.hint_business_name),
                leadingIcon = Icons.Default.Store
            )

            Spacer(modifier = Modifier.height(14.dp))

            StockProTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.lbl_email),
                placeholder = stringResource(R.string.hint_email),
                leadingIcon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            StockProTextField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(R.string.lbl_password),
                placeholder = stringResource(R.string.hint_password),
                leadingIcon = Icons.Default.Lock,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = StockProGreenPrimary
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            StockProTextField(
                value = phone,
                onValueChange = { phone = it },
                label = stringResource(R.string.lbl_phone),
                placeholder = stringResource(R.string.hint_phone),
                leadingIcon = Icons.Default.Phone
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Selector Tipo de Negocio
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = !expandedType },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedBusinessType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.lbl_business_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
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
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    businessTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedBusinessType = type
                                expandedType = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selector de País con soporte para Zona Euro (EUR)
            Text(
                text = stringResource(R.string.lbl_country),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth()
            )
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
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
                        selected = selectedCountry == code,
                        onClick = {
                            selectedCountry = code
                            when (code) {
                                "CO" -> selectedCurrency = "COP"
                                "US" -> selectedCurrency = "USD"
                                else -> selectedCurrency = "EUR"
                            }
                        },
                        label = { Text(label) }
                    )
                }
            }

            if (selectedCountry == "US") {
                Spacer(modifier = Modifier.height(10.dp))
                StockProTextField(
                    value = stateUs,
                    onValueChange = { stateUs = it },
                    label = stringResource(R.string.lbl_state_us),
                    placeholder = "CA, NY, TX"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (registerState is UiState.Loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = StockProGreenPrimary)
                }
            } else {
                StockProButton(
                    text = stringResource(R.string.title_register),
                    onClick = {
                        val finalOwner = if (ownerName.isBlank()) businessName else ownerName
                        viewModel.register(
                            nombrePropietario = finalOwner,
                            correo = email,
                            pass = password,
                            telefono = phone,
                            nombreNegocio = businessName,
                            tipoNegocio = selectedBusinessType,
                            pais = selectedCountry,
                            estadoRegion = stateUs,
                            monedaBase = selectedCurrency
                        )
                    },
                    testTag = "submit_register_button"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateBack)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.already_have_account),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = StockProGreenDark,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
