package com.example.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.StockProAppContainer
import com.example.ui.components.StockProTextField
import com.example.ui.theme.StockProGreenContainer
import com.example.ui.theme.StockProGreenPrimary
import com.example.ui.theme.StockProRedError
import com.example.ui.viewmodel.CategoriesViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onNavigateBack: () -> Unit,
    viewModel: CategoriesViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryDesc by remember { mutableStateOf("") }

    val categories = (categoriesState as? UiState.Success)?.data ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_categories), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = StockProGreenPrimary,
                contentColor = androidx.compose.ui.graphics.Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.btn_add_category))
            }
        }
    ) { innerPadding ->
        if (categoriesState is UiState.Loading && categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = StockProGreenPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { cat ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StockProGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Category, contentDescription = null, tint = StockProGreenPrimary)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = cat.nombre, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                cat.descripcion?.let { desc ->
                                    Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                            }
                            IconButton(onClick = { viewModel.deleteCategory(cat.id) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = StockProRedError)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.btn_add_category), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StockProTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = stringResource(R.string.lbl_category_name)
                    )
                    StockProTextField(
                        value = newCategoryDesc,
                        onValueChange = { newCategoryDesc = it },
                        label = stringResource(R.string.lbl_category_desc)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            viewModel.createCategory(newCategoryName, newCategoryDesc, "#087F5B")
                            newCategoryName = ""
                            newCategoryDesc = ""
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StockProGreenPrimary)
                ) {
                    Text(stringResource(R.string.btn_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}
