package rachman.forniandi.pretestbni_rachmanforniandi.presentation.home

import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.FinancialSummary
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.Transaction
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.TransactionType
import rachman.forniandi.pretestbni_rachmanforniandi.utils.DateUtils



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import rachman.forniandi.pretestbni_rachmanforniandi.utils.CurrencyUtils.formatCurrency
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val financialSummary by viewModel.financialSummary.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val transactions by viewModel.filteredTransactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Rekap keuanganmu",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            ActionButtonsSection(
                onTransferClick = {
                    navController.navigate("transaction/TRANSFER")
                },
                onTopupClick = {
                    navController.navigate("transaction/TOPUP")
                }
            )
        },

        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Financial Summary Section
            item {
                FinancialSummaryCard(financialSummary = financialSummary)
            }

            // Bar Chart Section (Bonus)
            item {
                BarChartSection(financialSummary = financialSummary)
            }

            // Tab Section
            item {
                TabSection(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        viewModel.setSelectedTab(tab)
                    }
                )
            }

            // Transaction List Section
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(transactions) { transaction ->
                    TransactionListItem(
                        transaction = transaction,
                        onClick = {
                            navController.navigate("receipt/${transaction.id}")
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FinancialSummaryCard(financialSummary: FinancialSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Income Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pemasukan",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Rp${formatCurrency(financialSummary.totalIncome)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expense Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pengeluaran",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Rp${formatCurrency(financialSummary.totalExpense)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFF44336)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color.LightGray)

            Spacer(modifier = Modifier.height(12.dp))

            // Difference Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Selisih",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                val difference = financialSummary.incomeExpenseDifference
                val sign = if (difference >= 0) "+" else ""
                val differenceColor = if (difference > 0) Color(0xFF4CAF50)
                else if (difference < 0) Color(0xFFF44336)
                else Color.Black

                Text(
                    text = "$sign Rp${formatCurrency(kotlin.math.abs(difference))}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = differenceColor
                )
            }
        }
    }
}

@Composable
fun BarChartSection(financialSummary: FinancialSummary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Persentase",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Gunakan LegendItem
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(
                color = Color(0xFF4CAF50),
                label = "Pemasukan",
                percentage = financialSummary.incomePercentage
            )

            LegendItem(
                color = Color(0xFFF44336),
                label = "Pengeluaran",
                percentage = financialSummary.expensePercentage
            )
        }

        // Bar Chart Vertikal
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            // Income Bar
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(
                        if (financialSummary.incomePercentage > 0f) {
                            (160.dp * financialSummary.incomePercentage).coerceAtLeast(4.dp)
                        } else {
                            4.dp
                        }
                    )
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color(0xFF4CAF50))
            )

            Spacer(modifier = Modifier.width(32.dp))

            // Expense Bar
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(
                        if (financialSummary.expensePercentage > 0f) {
                            (160.dp * financialSummary.expensePercentage).coerceAtLeast(4.dp)
                        } else {
                            4.dp
                        }
                    )
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color(0xFFF44336))
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, percentage: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, shape = RoundedCornerShape(2.dp))
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = "${(percentage * 100).roundToInt()}%",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun TabSection(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TabButton(
            text = "Pemasukan",
            isSelected = selectedTab == "INCOME",
            onClick = { onTabSelected("INCOME") },
            modifier = Modifier.weight(1f)
        )

        TabButton(
            text = "Pengeluaran",
            isSelected = selectedTab == "EXPENSE",
            onClick = { onTabSelected("EXPENSE") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Color(0xFF2196F3) else Color(0xFFF5F5F5)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun TransactionListItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
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
                    text = if (transaction.type == TransactionType.TOPUP) "Uang Masuk" else "Uang Keluar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = DateUtils.formatDateForDisplay(transaction.time),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = "Rp${formatCurrency(transaction.nominal)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (transaction.type == TransactionType.TOPUP)
                    Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
    }
}

    @Composable
    fun ActionButtonsSection(
        onTransferClick: () -> Unit,
        onTopupClick: () -> Unit
    ) {    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onTransferClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Transfer Saldo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onTopupClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Topup Saldo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

