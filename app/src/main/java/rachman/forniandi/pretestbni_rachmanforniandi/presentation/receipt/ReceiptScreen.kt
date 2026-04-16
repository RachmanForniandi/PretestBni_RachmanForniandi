package rachman.forniandi.pretestbni_rachmanforniandi.presentation.receipt


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.drawToBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.Transaction
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.TransactionStatus
import rachman.forniandi.pretestbni_rachmanforniandi.presentation.home.formatCurrency
import rachman.forniandi.pretestbni_rachmanforniandi.utils.ReceiptHelper
import rachman.forniandi.pretestbni_rachmanforniandi.utils.ReceiptHelper.getStatusTitle
import rachman.forniandi.pretestbni_rachmanforniandi.utils.ReceiptHelper.formatDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    navController: NavController,
    transactionId: Long,
    viewModel: ReceiptViewModel = hiltViewModel()
) {
    val transaction by viewModel.transaction.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Transaksi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Close button (X)
                    IconButton(
                        onClick = {
                            // Navigate to Home and clear back stack
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                transaction != null -> {
                    ReceiptContent(
                        transaction = transaction!!,
                        onShare = {
                            scope.launch {
                                val bitmap = view.drawToBitmap()
                                ReceiptHelper.shareReceipt(context, transaction!!, bitmap)
                            }
                        }
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Transaksi tidak ditemukan")
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptContent(
    transaction: Transaction,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Wondr Logo
        Text(
            text = "wondr",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF6B00), // Orange color
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "by BNI",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Status Icon and Title
        val isSuccess = transaction.status == TransactionStatus.SUCCESS
        val statusIcon = if (isSuccess) "✅" else "❌"
        val statusTitle = getStatusTitle(transaction.type, isSuccess)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = statusTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
            Text(
                text = " $statusIcon",
                fontSize = 20.sp
            )
        }

        // Nominal
        Text(
            text = "Rp${formatCurrency(transaction.nominal)}",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Date and Time
        Text(
            text = formatDateTime(transaction.time),
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Reference ID
        Text(
            text = "Ref ID: ${transaction.reffId}",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Detail Transaksi Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8F9FA)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Detail transaksi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Nominal Row
                DetailRow(
                    label = "Nominal",
                    value = "Rp${formatCurrency(transaction.nominal)}"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Admin Fee Row
                DetailRow(
                    label = "Biaya admin",
                    value = "Rp0"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Divider(color = Color.LightGray)

                Spacer(modifier = Modifier.height(12.dp))

                // Total Row
                DetailRow(
                    label = "Total",
                    value = "Rp${formatCurrency(transaction.nominal)}",
                    isTotal = true
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Share Button (only show when status is SUCCESS)
        if (transaction.status == TransactionStatus.SUCCESS) {
            Button(
                onClick = onShare,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B00)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Bagikan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (isTotal) Color.Black else Color.Gray,
            fontWeight = if (isTotal) FontWeight.Medium else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = if (isTotal) Color.Black else Color.Black,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
    }
}

