package rachman.forniandi.pretestbni_rachmanforniandi.presentation.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import rachman.forniandi.pretestbni_rachmanforniandi.domain.ITransactionRepository
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.FinancialSummary
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ITransactionRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow("INCOME")
    val selectedTab = _selectedTab.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val financialSummary = repository.getFinancialSummary()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FinancialSummary(0.0,
                0.0,
                0.0,
                0.5f,
                0.5f)
        )

    val filteredTransactions = _selectedTab.flatMapLatest { tab ->
        when (tab) {
            "INCOME" -> repository.getIncomeTransactions(limit = 3)
            else -> repository.getExpenseTransactions(limit = 3)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(500)
            _isLoading.value = false
        }
    }
}