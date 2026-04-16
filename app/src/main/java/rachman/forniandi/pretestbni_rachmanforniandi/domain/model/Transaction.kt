package rachman.forniandi.pretestbni_rachmanforniandi.domain.model

import java.util.Date

data class Transaction(
    val id: Long = 0,
    val reffId: String,
    val time: Date,
    val nominal: Double,
    val type: TransactionType,
    val status: TransactionStatus
)

enum class TransactionType {
    TRANSFER, TOPUP
}

enum class TransactionStatus {
    SUCCESS, FAILED, PENDING
}
