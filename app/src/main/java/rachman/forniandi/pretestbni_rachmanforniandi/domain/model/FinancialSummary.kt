package rachman.forniandi.pretestbni_rachmanforniandi.domain.model

data class FinancialSummary(
    val totalIncome: Double,
    val totalExpense: Double,
    val incomeExpenseDifference: Double,
    val incomePercentage: Float,
    val expensePercentage: Float
)
