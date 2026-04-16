// presentation/transaction/GatheringScreen.kt
package rachman.forniandi.pretestbni_rachmanforniandi.presentation.transaction

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
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
import rachman.forniandi.pretestbni_rachmanforniandi.presentation.gathering.GatheringViewModel
import rachman.forniandi.pretestbni_rachmanforniandi.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GatheringScreen(
    navController: NavController,
    transactionType: String,
    viewModel: GatheringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(transactionType) {
        viewModel.setTransactionType(transactionType)
        delay(100)
        focusRequester.requestFocus()
    }

    LaunchedEffect(uiState.transactionCompleted) {
        if (uiState.transactionCompleted && uiState.transactionId != null) {
            navController.navigate("receipt/${uiState.transactionId}") {
                popUpTo("home") { inclusive = false }
            }
        }
    }

    BackHandler(enabled = uiState.isLoading) {
        // Block back button when loading
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

                Text(
                    text = "Nominal",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                AmountInputFieldFixed(
                    value = uiState.nominalInput,
                    onValueChange = { viewModel.updateNominalInput(it) },
                    focusRequester = focusRequester,
                    onClear = { viewModel.clearNominalInput() },
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = Color(0xFFF44336),
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (transactionType == "TRANSFER") {
                    SourceOfFundSection(balance = balance)
                }
            }
        }

        if (uiState.isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
fun AmountInputFieldFixed(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onClear: () -> Unit,
    enabled: Boolean = true
) {
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
            textStyle = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) Color.Black else Color.Gray
            ),
            singleLine = true,
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
                        color = if (!enabled) Color.Gray else Color.Black
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(modifier = Modifier.weight(1f)) {
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
                .clickable(enabled = false) { },
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
                        text = CurrencyUtils.formatRupiah(balance),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

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