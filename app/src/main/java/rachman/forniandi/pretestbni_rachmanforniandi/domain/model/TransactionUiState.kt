package rachman.forniandi.pretestbni_rachmanforniandi.domain.model

data class TransactionUiState(
    val transactionType: String = "",
    val nominalInput: String = "",
    val nominalAmount: Double = 0.0,
    val errorMessage: String? = null,
    val isValid: Boolean = false,
    val isLoading: Boolean = false,
    val transactionCompleted: Boolean = false,
    val transactionId: Long? = null
)
