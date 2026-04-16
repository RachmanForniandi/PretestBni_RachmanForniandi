package rachman.forniandi.pretestbni_rachmanforniandi.data.remote.api

import rachman.forniandi.pretestbni_rachmanforniandi.data.remote.dto.TransactionRequest
import rachman.forniandi.pretestbni_rachmanforniandi.data.remote.dto.TransactionResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionService {
    @POST("execution")
    suspend fun executeTransaction(
        @Body request: TransactionRequest
    ): TransactionResponse
}