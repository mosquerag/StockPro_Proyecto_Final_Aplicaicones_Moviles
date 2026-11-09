package com.example.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.StockProAppContainer
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FilterChipItem
import com.example.ui.components.ProductRowItem
import com.example.ui.components.StockProTextField
import com.example.ui.theme.StockProGreenPrimary
import com.example.ui.viewmodel.ProductsViewModel
import com.example.ui.viewmodel.StockProViewModelFactory
import com.example.ui.viewmodel.UiState
import com.example.ui.util.Formatters
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onNavigateToNewProduct: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    currencyCode: String = "COP",
    viewModel: ProductsViewModel = viewModel(
        factory = StockProViewModelFactory(StockProAppContainer.getInstance(LocalContext.current))
    )
) {
    val cachedProducts by viewModel.cachedProducts.collectAsStateWithLifecycle()
    val productsState by viewModel.productsState.collectAsStateWithLifecycle()
    val categoriesList by viewModel.categories.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }

    // Usar datos remotos si están disponibles, sino usar caché de Room
    val productList = (productsState as? UiState.Success)?.data?.map { dto ->
        ProductDisplayItem(
            id = dto.id,
            name = dto.nombre,
            category = dto.categoriaNombre ?: "",
            categoryId = dto.categoriaId,
            price = BigDecimal.valueOf(dto.precioVenta),
            stock = dto.stockActual.toInt(),
            unit = dto.unidadMedida,
            isLowStock = dto.stockBajo
        )
    } ?: cachedProducts.map { entity ->
        ProductDisplayItem(
            id = entity.id,
            name = entity.nombre,
            category = entity.categoriaNombre ?: "",
            categoryId = entity.categoriaId,
            price = BigDecimal.valueOf(entity.precioVenta),
            stock = entity.stockActual.toInt(),
            unit = entity.unidadMedida,
            isLowStock = entity.stockBajo
        )
    }

    val filteredProducts = remember(searchQuery, selectedCategoryId, productList) {
        productList.filter { prod ->
            val matchesSearch = prod.name.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategoryId == null || prod.categoryId == selectedCategoryId
            matchesSearch && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_products), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewProduct,
                containerColor = StockProGreenPrimary,
                contentColor = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.testTag("add_product_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.btn_new_product))
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
            // Buscador
            StockProTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = stringResource(R.string.search_product_hint),
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Chips horizontales de categorías provenientes del backend
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    FilterChipItem(
                        label = stringResource(R.string.tab_all),
                        isSelected = selectedCategoryId == null,
                        onClick = { selectedCategoryId = null }
                    )
                }
                items(categoriesList) { cat ->
                    FilterChipItem(
                        label = cat.nombre,
                        isSelected = selectedCategoryId == cat.id,
                        onClick = { selectedCategoryId = cat.id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (productsState is UiState.Loading && productList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = StockProGreenPrimary)
                }
            } else if (filteredProducts.isEmpty()) {
                EmptyStateView(
                    title = stringResource(R.string.empty_products_title),
                    description = stringResource(R.string.empty_products_subtitle),
                    actionButtonText = stringResource(R.string.btn_new_product),
                    onActionClick = onNavigateToNewProduct
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredProducts) { item ->
                        ProductRowItem(
                            name = item.name,
                            category = item.category,
                            priceFormatted = Formatters.formatCurrency(item.price, currencyCode),
                            stock = "${item.stock} ${item.unit}",
                            isLowStock = item.isLowStock,
                            onClick = { onNavigateToProductDetail(item.id) }
                        )
                    }
                }
            }
        }
    }
}

private data class ProductDisplayItem(
    val id: String,
    val name: String,
    val category: String,
    val categoryId: String?,
    val price: BigDecimal,
    val stock: Int,
    val unit: String,
    val isLowStock: Boolean
)
