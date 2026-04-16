package rachman.forniandi.pretestbni_rachmanforniandi.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rachman.forniandi.pretestbni_rachmanforniandi.domain.ITransactionRepository
import rachman.forniandi.pretestbni_rachmanforniandi.domain.usecase.GetTransactionHistoryUseCase
import rachman.forniandi.pretestbni_rachmanforniandi.domain.usecase.ProcessTransactionUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideProcessTransactionUseCase(
        repository: ITransactionRepository
    ): ProcessTransactionUseCase {
        return ProcessTransactionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTransactionHistoryUseCase(
        repository: ITransactionRepository
    ): GetTransactionHistoryUseCase {
        return GetTransactionHistoryUseCase(repository)
    }
}