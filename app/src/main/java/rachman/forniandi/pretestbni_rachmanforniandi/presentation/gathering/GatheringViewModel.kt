package rachman.forniandi.pretestbni_rachmanforniandi.presentation.gathering

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rachman.forniandi.pretestbni_rachmanforniandi.domain.ITransactionRepository
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.TransactionUiState
import rachman.forniandi.pretestbni_rachmanforniandi.domain.usecase.ProcessTransactionUseCase
import javax.inject.Inject

@HiltViewModel
class GatheringViewModel @Inject constructor(
    private val processTransactionUseCase: ProcessTransactionUseCase,
    private val repository: ITransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState = _uiState.asStateFlow()

    val balance = repository.getFinancialSummary()
        .map { it.incomeExpenseDifference }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    fun setTransactionType(type: String) {
        _uiState.update { it.copy(transactionType = type) }
    }

    fun updateNominalInput(input: String) {
        val filteredInput = input.filter { it.isDigit() }

        val amount = filteredInput.toDoubleOrNull() ?: 0.0
        val errorMessage = validateAmount(amount)

        _uiState.update {
            it.copy(
                nominalInput = filteredInput,
                nominalAmount = amount,
                errorMessage = errorMessage,
                isValid = errorMessage == null && amount > 0
            )
        }
    }

    fun clearNominalInput() {
        _uiState.update {
            it.copy(
                nominalInput = "",
                nominalAmount = 0.0,
                errorMessage = null,
                isValid = false
            )
        }
    }

    private fun validateAmount(amount: Double): String? {
        return when {
            amount < 10000 -> "Minimal amount adalah Rp10.000"
            amount > 1000000000 -> "Maksimal amount adalah Rp1.000.000.000"
            _uiState.value.transactionType == "TRANSFER" && amount > balance.value ->
                "Saldo Anda tidak mencukupi"
            else -> null
        }
    }

    fun processTransaction() {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }

                // Add small delay for better UX
                delay(500)

                val transaction = processTransactionUseCase(
                    _uiState.value.nominalAmount,
                    _uiState.value.transactionType
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        transactionCompleted = true,
                        transactionId = transaction.id
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Transaction failed"
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { TransactionUiState() }
    }
}