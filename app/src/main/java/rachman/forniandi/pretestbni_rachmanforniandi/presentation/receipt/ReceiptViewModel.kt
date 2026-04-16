package rachman.forniandi.pretestbni_rachmanforniandi.presentation.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rachman.forniandi.pretestbni_rachmanforniandi.domain.ITransactionRepository
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.Transaction
import javax.inject.Inject

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val repository: ITransactionRepository
) : ViewModel() {

    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction = _transaction.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val tx = repository.getTransactionById(id)
                if (tx != null) {
                    _transaction.value = tx
                } else {
                    _error.value = "Transaksi tidak ditemukan"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal memuat transaksi"
                _transaction.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearTransaction() {
        _transaction.value = null
        _error.value = null
    }

    fun retryLoadTransaction(id: Long) {
        loadTransaction(id)
    }
}