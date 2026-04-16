package rachman.forniandi.pretestbni_rachmanforniandi.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import rachman.forniandi.pretestbni_rachmanforniandi.data.local.TransactionEntity
import rachman.forniandi.pretestbni_rachmanforniandi.data.local.dao.TransactionDao
import rachman.forniandi.pretestbni_rachmanforniandi.utils.Constants
import rachman.forniandi.pretestbni_rachmanforniandi.utils.DateConverter

@Database(
    entities = [TransactionEntity::class],
    version = Constants.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}