package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.StockProAppContainer
import com.example.ui.components.StockProButton
import com.example.ui.components.StockProSecondaryButton
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

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("demo@stockpro.com") }
    var password by remember { mutableStateOf("Password123*") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Logo del Cubo StockPro
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(StockProGreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                tint = StockProGreenPrimary,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = StockProGreenDark
            )
        )

        Text(
            text = stringResource(R.string.tagline),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.login_welcome_desc),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mensaje de error si la llamada falla
        if (loginState is UiState.Error) {
            val errorCode = (loginState as UiState.Error).code
            val errorMessage = ErrorTranslator.translate(context, errorCode)
            Card(
                colors = CardDefaults.cardColors(containerColor = StockProRedContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = StockProRedError,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        StockProTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.lbl_email),
            placeholder = stringResource(R.string.hint_email),
            leadingIcon = Icons.Default.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        StockProTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.lbl_password),
            placeholder = stringResource(R.string.hint_password),
            leadingIcon = Icons.Default.Lock,
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

        Spacer(modifier = Modifier.height(24.dp))

        if (loginState is UiState.Loading) {
            CircularProgressIndicator(
                color = StockProGreenPrimary,
                modifier = Modifier.size(48.dp)
            )
        } else {
            StockProButton(
                text = stringResource(R.string.btn_login),
                onClick = { viewModel.login(email, password) },
                testTag = "login_button"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        StockProSecondaryButton(
            text = stringResource(R.string.btn_register),
            onClick = onNavigateToRegister,
            testTag = "register_navigation_button"
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
