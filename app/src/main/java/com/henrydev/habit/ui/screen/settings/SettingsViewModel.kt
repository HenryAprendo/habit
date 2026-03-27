package com.henrydev.habit.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.habit.domain.subscription.model.UserStatus
import com.henrydev.habit.domain.subscription.repository.SubscriptionRepository
import com.henrydev.habit.domain.use_cases.ExportDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val exportDataUseCase: ExportDataUseCase
): ViewModel() {

    private val _exportStatus = MutableStateFlow<ExportStatus>(ExportStatus.Idle)
    val exportStatus: StateFlow<ExportStatus> = _exportStatus.asStateFlow()

    val uiState: StateFlow<SettingsUiState> =
        subscriptionRepository.getUserStatus()
            .map { status -> SettingsUiState(status) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState()
            )

    fun startExport() {
        viewModelScope.launch {
            _exportStatus.value = ExportStatus.Loading
            try {
                val jsonString = exportDataUseCase()
                _exportStatus.value = ExportStatus.Success(jsonString)
            } catch (e:Exception) {
                _exportStatus.value = ExportStatus.Error(e.message ?: "Unknow error")
            }
        }
    }

    fun resetExportStatus() {
        _exportStatus.value = ExportStatus.Idle
    }

}

data class SettingsUiState(
    val userStatus: UserStatus = UserStatus.Free
)

/**
 * Representa los diferentes estados del proceso de Backup
 */
sealed interface ExportStatus {
    data object Idle : ExportStatus
    data object Loading : ExportStatus
    data class Success(val jsonData: String) : ExportStatus
    data class Error(val message: String): ExportStatus
}



