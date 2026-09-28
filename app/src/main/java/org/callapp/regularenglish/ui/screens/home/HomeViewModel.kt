package org.callapp.regularenglish.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.callapp.regularenglish.domain.usecase.GetLearningContentUseCase
import javax.inject.Inject

@HiltViewModel class HomeViewModel @Inject constructor(content: GetLearningContentUseCase) : ViewModel() {
    val topics = content.topics().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
