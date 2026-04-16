package rachman.forniandi.pretestbni_rachmanforniandi.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TransactionRequest(
    @SerializedName("reffId")
    val reffId: String,

    @SerializedName("time")
    val time: String,

    @SerializedName("nominal")
    val nominal: Double,

    @SerializedName("type")
    val type: String
)

data class TransactionResponse(
    @SerializedName("status")
    val status: String
)
