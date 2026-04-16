package rachman.forniandi.pretestbni_rachmanforniandi.domain.usecase

import rachman.forniandi.pretestbni_rachmanforniandi.domain.ITransactionRepository
import javax.inject.Inject

class ProcessTransactionUseCase @Inject constructor(
    private val repository: ITransactionRepository
) {
    suspend operator fun invoke(nominal: Double, type: String) =
        repository.processTransaction(nominal, type)
}