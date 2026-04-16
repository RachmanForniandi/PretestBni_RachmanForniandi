package rachman.forniandi.pretestbni_rachmanforniandi.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rachman.forniandi.pretestbni_rachmanforniandi.data.remote.api.TransactionService
import rachman.forniandi.pretestbni_rachmanforniandi.data.remote.service.RetrofitInstance
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(): RetrofitInstance = RetrofitInstance()

    @Provides
    @Singleton
    fun provideTransactionApi(retrofitInstance: RetrofitInstance): TransactionService =
        retrofitInstance.transactionService
}