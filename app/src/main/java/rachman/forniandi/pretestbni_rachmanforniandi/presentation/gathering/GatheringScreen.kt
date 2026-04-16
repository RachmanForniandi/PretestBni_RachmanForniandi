package rachman.forniandi.pretestbni_rachmanforniandi.presentation.gathering

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import rachman.forniandi.pretestbni_rachmanforniandi.presentation.home.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GatheringScreen(
    navController: NavController,
    transactionType: String,
    viewModel: GatheringViewModel = hiltViewModel()
){


    val uiState by viewModel.uiState.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // Set transaction type when screen loads
    LaunchedEffect(transactionType) {
        viewModel.setTransactionType(transactionType)
        delay(100)
        focusRequester.requestFocus()
    }

    // Handle navigation when transaction is complete
    LaunchedEffect(uiState.transactionCompleted) {
        if (uiState.transactionCompleted && uiState.transactionId != null) {
            navController.navigate("receipt/${uiState.transactionId}") {
                popUpTo("home") { inclusive = false }
            }
        }
    }

    // Block back button when loading
    BackHandler(enabled = uiState.isLoading) {
        // Do nothing - block back button
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Content
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (transactionType == "TRANSFER") "Transfer Saldo" else "Topup Saldo",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (!uiState.isLoading) {
                                    navController.navigateUp()
                                }
                            },
                            enabled = !uiState.isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = if (uiState.isLoading) Color.Gray else Color.Black
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black
                    )
                )
            },
            containerColor = Color.White,
            bottomBar = {
                BottomButtonSection(
                    transactionType = transactionType,
                    isValid = uiState.isValid,
                    isLoading = uiState.isLoading,
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.processTransaction()
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Nominal Label
                Text(
                    text = "Nominal",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Amount Input Field
                AmountInputField(
                    value = uiState.nominalInput,
                    onValueChange = { viewModel.updateNominalInput(it) },
                    errorMessage = uiState.errorMessage,
                    focusRequester = focusRequester,
                    onClear = { viewModel.clearNominalInput() },
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Error Message
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = Color(0xFFF44336),
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Source of Fund Section (only for TRANSFER)
                if (transactionType == "TRANSFER") {
                    SourceOfFundSection(balance = balance)
                }
            }
        }

        // Loading Overlay
        if (uiState.isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
fun LoadingOverlay() {
    Dialog(
        onDismissRequest = { /* Prevent dismiss */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) { /* Block clicks */ },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFF2196F3),
                        strokeWidth = 4.dp
                    )
                }
            }
        }
    }
}

@Composable
fun AmountInputField(
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String?,
    focusRequester: FocusRequester,
    onClear: () -> Unit,
    enabled: Boolean = true
) {
    val displayValue = if (value.isNotEmpty()) {
        try {
            val amount = value.toDoubleOrNull() ?: 0.0
            formatCurrency(amount)
        } catch (e: Exception) {
            value
        }
    } else {
        ""
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (enabled) Color(0xFFF5F5F5) else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rp",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (value.isEmpty() || !enabled) Color.Gray else Color.Black
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = "0",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        } else {
                            Text(
                                text = displayValue,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (enabled) Color.Black else Color.Gray
                            )
                        }
                        innerTextField()
                    }

                    if (value.isNotEmpty() && enabled) {
                        IconButton(onClick = onClear) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun SourceOfFundSection(balance: Double) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sumber dana",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Saldo",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Rp${formatCurrency(balance)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                // Radio button indicator
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = Color(0xFF2196F3),
                            shape = RoundedCornerShape(10.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun BottomButtonSection(
    transactionType: String,
    isValid: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            enabled = isValid && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (transactionType == "TRANSFER")
                    Color(0xFF2196F3) else Color(0xFF4CAF50),
                disabledContainerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (transactionType == "TRANSFER") "Transfer" else "Topup",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}