package rachman.forniandi.pretestbni_rachmanforniandi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.Transaction
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.TransactionStatus
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.TransactionType
import java.util.Date

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reffId: String,
    val time: Date,
    val nominal: Double,
    val type: String,
    val status: String
) {
    fun toDomain() = Transaction(
        id = id,
        reffId = reffId,
        time = time,
        nominal = nominal,
        type = TransactionType.valueOf(type),
        status = TransactionStatus.valueOf(status)
    )

    companion object {
        fun fromDomain(transaction: Transaction) = TransactionEntity(
            id = transaction.id,
            reffId = transaction.reffId,
            time = transaction.time,
            nominal = transaction.nominal,
            type = transaction.type.name,
            status = transaction.status.name
        )
    }
}
