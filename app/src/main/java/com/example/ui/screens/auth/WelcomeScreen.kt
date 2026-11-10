package com.example.ui.screens.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.StockProButton
import com.example.ui.components.StockProSecondaryButton
import com.example.ui.theme.StockProGreenContainer
import com.example.ui.theme.StockProGreenDark
import com.example.ui.theme.StockProGreenPrimary

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Logo Ícono del Cubo StockPro
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(StockProGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    tint = StockProGreenPrimary,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = StockProGreenDark
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.tagline),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.login_welcome_desc),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Principal: Iniciar sesión
            StockProButton(
                text = stringResource(R.string.btn_login),
                onClick = onNavigateToLogin,
                testTag = "welcome_login_button"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Botón Secundario: Crear cuenta
            StockProSecondaryButton(
                text = stringResource(R.string.btn_register),
                onClick = onNavigateToRegister,
                testTag = "welcome_register_button"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .clickable(onClick = onNavigateToLogin)
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
        }

        // Ilustración gráfica de la tienda (Storefront Vector Canvas)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(bottom = 12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Pared de la tienda
                val storeW = w * 0.7f
                val storeH = h * 0.65f
                val storeLeft = (w - storeW) / 2
                val storeTop = h - storeH

                drawRoundRect(
                    color = Color(0xFFF1F3F5),
                    topLeft = Offset(storeLeft, storeTop),
                    size = Size(storeW, storeH),
                    cornerRadius = CornerRadius(12f, 12f)
                )

                // Techo / Toldo verde (Awning)
                val awningH = storeH * 0.3f
                val stripeCount = 7
                val stripeW = storeW / stripeCount
                val awningPath = Path().apply {
                    moveTo(storeLeft - 10f, storeTop)
                    lineTo(storeLeft + storeW + 10f, storeTop)
                    lineTo(storeLeft + storeW + 10f, storeTop + awningH)
                    lineTo(storeLeft - 10f, storeTop + awningH)
                    close()
                }
                drawPath(awningPath, color = Color(0xFF087F5B))

                // Franjas blancas del toldo
                for (i in 0 until stripeCount) {
                    if (i % 2 == 1) {
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(storeLeft - 10f + i * stripeW, storeTop),
                            size = Size(stripeW, awningH)
                        )
                    }
                }

                // Puerta de la tienda
                val doorW = storeW * 0.28f
                val doorH = storeH * 0.55f
                val doorLeft = storeLeft + (storeW - doorW) / 2
                val doorTop = h - doorH
                drawRoundRect(
                    color = Color(0xFF343A40),
                    topLeft = Offset(doorLeft, doorTop),
                    size = Size(doorW, doorH),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // Ventanas laterales
                val windowW = storeW * 0.22f
                val windowH = storeH * 0.35f
                val windowTop = storeTop + awningH + 12f

                // Ventana Izquierda
                drawRoundRect(
                    color = Color(0xFFA5D8FF),
                    topLeft = Offset(storeLeft + 16f, windowTop),
                    size = Size(windowW, windowH),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // Ventana Derecha
                drawRoundRect(
                    color = Color(0xFFA5D8FF),
                    topLeft = Offset(storeLeft + storeW - windowW - 16f, windowTop),
                    size = Size(windowW, windowH),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // Marco/Suelo de la tienda
                drawRect(
                    color = Color(0xFF212529),
                    topLeft = Offset(0f, h - 6f),
                    size = Size(w, 6f)
                )
            }
        }
    }
}
