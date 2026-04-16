package rachman.forniandi.pretestbni_rachmanforniandi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import rachman.forniandi.pretestbni_rachmanforniandi.data.local.TransactionEntity

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("SELECT * FROM transactions ORDER BY time DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE type = :type AND status = 'SUCCESS' ORDER BY time DESC LIMIT :limit")
    fun getTransactionsByType(type: String, limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(nominal) FROM transactions WHERE type = 'TOPUP' AND status = 'SUCCESS'")
    suspend fun getTotalIncome(): Double?

    @Query("SELECT SUM(nominal) FROM transactions WHERE type = 'TRANSFER' AND status = 'SUCCESS'")
    suspend fun getTotalExpense(): Double?
}