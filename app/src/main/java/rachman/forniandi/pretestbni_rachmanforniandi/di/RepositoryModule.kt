package rachman.forniandi.pretestbni_rachmanforniandi.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rachman.forniandi.pretestbni_rachmanforniandi.data.repository.TransactionRepositoryImpl
import rachman.forniandi.pretestbni_rachmanforniandi.domain.ITransactionRepository
import rachman.forniandi.pretestbni_rachmanforniandi.domain.usecase.GetTransactionHistoryUseCase
import rachman.forniandi.pretestbni_rachmanforniandi.domain.usecase.ProcessTransactionUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): ITransactionRepository
}

