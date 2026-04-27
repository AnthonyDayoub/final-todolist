package com.example.csci215_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci215_final.domain.model.Distraction
import com.example.csci215_final.domain.model.Helper
import com.example.csci215_final.repository.DistractionRepository
import com.example.csci215_final.repository.HelperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HelperDistractionUiState(
    val newHelperName: String = "",
    val newHelperDescription: String = "",
    val newDistractionName: String = "",
    val newDistractionDescription: String = "",
    val error: String? = null
)

class HelperDistractionViewModel(
    private val helperRepository: HelperRepository,
    private val distractionRepository: DistractionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HelperDistractionUiState())
    val uiState: StateFlow<HelperDistractionUiState> = _uiState.asStateFlow()

    val allHelpers: StateFlow<List<Helper>> =
        helperRepository.getAllHelpers().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val allDistractions: StateFlow<List<Distraction>> =
        distractionRepository.getAllDistractions().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // ──────────── Helper form fields ────────────

    fun onNewHelperNameChange(name: String) {
        _uiState.value = _uiState.value.copy(newHelperName = name)
    }

    fun onNewHelperDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(newHelperDescription = description)
    }

    fun addHelper() {
        val state = _uiState.value
        if (state.newHelperName.isBlank()) {
            _uiState.value = state.copy(error = "Helper name cannot be empty")
            return
        }
        viewModelScope.launch {
            helperRepository.insertHelper(
                Helper(name = state.newHelperName.trim(), description = state.newHelperDescription.trim())
            )
            _uiState.value = _uiState.value.copy(newHelperName = "", newHelperDescription = "", error = null)
        }
    }

    fun deleteHelper(helper: Helper) {
        viewModelScope.launch { helperRepository.deleteHelper(helper) }
    }

    fun updateHelper(helper: Helper) {
        viewModelScope.launch { helperRepository.updateHelper(helper) }
    }

    // ──────────── Distraction form fields ────────────

    fun onNewDistractionNameChange(name: String) {
        _uiState.value = _uiState.value.copy(newDistractionName = name)
    }

    fun onNewDistractionDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(newDistractionDescription = description)
    }

    fun addDistraction() {
        val state = _uiState.value
        if (state.newDistractionName.isBlank()) {
            _uiState.value = state.copy(error = "Distraction name cannot be empty")
            return
        }
        viewModelScope.launch {
            distractionRepository.insertDistraction(
                Distraction(name = state.newDistractionName.trim(), description = state.newDistractionDescription.trim())
            )
            _uiState.value = _uiState.value.copy(newDistractionName = "", newDistractionDescription = "", error = null)
        }
    }

    fun deleteDistraction(distraction: Distraction) {
        viewModelScope.launch { distractionRepository.deleteDistraction(distraction) }
    }

    fun updateDistraction(distraction: Distraction) {
        viewModelScope.launch { distractionRepository.updateDistraction(distraction) }
    }
}
