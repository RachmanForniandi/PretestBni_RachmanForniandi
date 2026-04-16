package rachman.forniandi.pretestbni_rachmanforniandi.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import rachman.forniandi.pretestbni_rachmanforniandi.data.local.TransactionEntity
import rachman.forniandi.pretestbni_rachmanforniandi.data.local.dao.TransactionDao
import rachman.forniandi.pretestbni_rachmanforniandi.data.remote.api.TransactionService
import rachman.forniandi.pretestbni_rachmanforniandi.data.remote.dto.TransactionRequest
import rachman.forniandi.pretestbni_rachmanforniandi.domain.ITransactionRepository
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.FinancialSummary
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.Transaction
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.TransactionStatus
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.TransactionType
import rachman.forniandi.pretestbni_rachmanforniandi.utils.DateUtils
import rachman.forniandi.pretestbni_rachmanforniandi.utils.DateUtils.generateReffId
import java.util.Date
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionService: TransactionService,
    private val transactionDao: TransactionDao
) : ITransactionRepository {
    override suspend fun processTransaction(
        nominal: Double,
        type: String
    ): Transaction {
        val reffId = generateReffId()
        val currentTime = Date()
        val formattedTime = DateUtils.formatDateForApi(currentTime)

        val request = TransactionRequest(
            reffId = reffId,
            time = formattedTime,
            nominal = nominal,
            type = type
        )

        val response = try {
            transactionService.executeTransaction(request)
        } catch (e: Exception) {
            throw e
        }

        val transaction = Transaction(
            reffId = reffId,
            time = currentTime,
            nominal = nominal,
            type = TransactionType.valueOf(type),
            status = if (response.status == "SUCCESS") TransactionStatus.SUCCESS else TransactionStatus.FAILED
        )

        val id = transactionDao.insertTransaction(TransactionEntity.fromDomain(transaction))
        return transaction.copy(id = id)
    }

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getTransactionById(id)?.toDomain()
    }

    override fun getIncomeTransactions(limit: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(TransactionType.TOPUP.name, limit)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getExpenseTransactions(limit: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(TransactionType.TRANSFER.name, limit)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getFinancialSummary(): Flow<FinancialSummary> {
        return transactionDao.getAllTransactions().map { entities ->
            val transactions = entities.map { it.toDomain() }

            val totalIncome = transactions
                .filter { it.type == TransactionType.TOPUP && it.status == TransactionStatus.SUCCESS }
                .sumOf { it.nominal }

            val totalExpense = transactions
                .filter { it.type == TransactionType.TRANSFER && it.status == TransactionStatus.SUCCESS }
                .sumOf { it.nominal }

            val total = totalIncome + totalExpense
            val difference = totalIncome - totalExpense

            val incomePercentage = if (total > 0) (totalIncome / total).toFloat() else 0f
            val expensePercentage = if (total > 0) (totalExpense / total).toFloat() else 0f

            FinancialSummary(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                incomeExpenseDifference = difference,
                incomePercentage = incomePercentage,
                expensePercentage = expensePercentage
            )
        }
    }
}