package rachman.forniandi.pretestbni_rachmanforniandi.domain

import kotlinx.coroutines.flow.Flow
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.FinancialSummary
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.Transaction

interface ITransactionRepository {
    suspend fun processTransaction(
        nominal: Double,
        type: String
    ): Transaction

    fun getAllTransactions(): Flow<List<Transaction>>

    suspend fun getTransactionById(id: Long): Transaction?

    fun getIncomeTransactions(limit: Int = 3): Flow<List<Transaction>>

    fun getExpenseTransactions(limit: Int = 3): Flow<List<Transaction>>

    fun getFinancialSummary(): Flow<FinancialSummary>
}