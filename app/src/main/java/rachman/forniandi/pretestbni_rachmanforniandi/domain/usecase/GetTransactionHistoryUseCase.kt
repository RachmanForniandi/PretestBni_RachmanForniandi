package rachman.forniandi.pretestbni_rachmanforniandi.domain.usecase

import kotlinx.coroutines.flow.Flow
import rachman.forniandi.pretestbni_rachmanforniandi.domain.ITransactionRepository
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.Transaction
import javax.inject.Inject

class GetTransactionHistoryUseCase @Inject constructor(
    private val repository: ITransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> {
        return repository.getAllTransactions()
    }
}