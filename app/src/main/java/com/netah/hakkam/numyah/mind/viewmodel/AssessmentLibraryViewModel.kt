package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.app.CurrentLocaleProvider
import com.netah.hakkam.numyah.mind.domain.usecase.GetCurrentQuestionnaireUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.ObserveActiveAssessmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed interface AssessmentLibraryUiState {
    data object Loading : AssessmentLibraryUiState
    data class Loaded(val model: AssessmentLibraryUiModel) : AssessmentLibraryUiState
    data object Error : AssessmentLibraryUiState
}

data class AssessmentLibraryUiModel(
    val entry: AssessmentLibraryEntryUiModel
)

data class AssessmentLibraryEntryUiModel(
    val title: String,
    val sephiraCount: Int,
    val activeAssessment: ActiveAssessmentUiModel?
)

@HiltViewModel
class AssessmentLibraryViewModel @Inject constructor(
    private val getCurrentQuestionnaireUseCase: GetCurrentQuestionnaireUseCase,
    private val observeActiveAssessmentUseCase: ObserveActiveAssessmentUseCase,
    private val currentLocaleProvider: CurrentLocaleProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<AssessmentLibraryUiState>(AssessmentLibraryUiState.Loading)
    val uiState: StateFlow<AssessmentLibraryUiState> = _uiState.asStateFlow()

    init {
        observeLibrary()
    }

    private fun observeLibrary() {
        viewModelScope.launch {
            try {
                combine(
                    getCurrentQuestionnaireUseCase.run(currentLocaleProvider.current()),
                    observeActiveAssessmentUseCase.run()
                ) { questionnaire, activeSnapshot ->
                    AssessmentLibraryUiModel(
                        entry = AssessmentLibraryEntryUiModel(
                            title = questionnaire.title,
                            sephiraCount = questionnaire.sections.size,
                            activeAssessment = activeSnapshot?.let {
                                buildActiveAssessmentUiModel(
                                    questionnaire = questionnaire,
                                    snapshot = it
                                )
                            }
                        )
                    )
                }.collect { model ->
                    _uiState.value = AssessmentLibraryUiState.Loaded(model)
                }
            } catch (_: Throwable) {
                _uiState.value = AssessmentLibraryUiState.Error
            }
        }
    }
}
