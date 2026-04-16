package rachman.forniandi.pretestbni_rachmanforniandi.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import rachman.forniandi.pretestbni_rachmanforniandi.data.local.dao.TransactionDao
import rachman.forniandi.pretestbni_rachmanforniandi.data.local.db.AppDatabase
import rachman.forniandi.pretestbni_rachmanforniandi.utils.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao =
        database.transactionDao()
}