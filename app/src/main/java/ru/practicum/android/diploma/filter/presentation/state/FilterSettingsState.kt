package ru.practicum.android.diploma.filter.presentation.state

import ru.practicum.android.diploma.filter.domain.model.FilterGeneral

sealed class FilterSettingsState {
    data class Filter(val filter: FilterGeneral) : FilterSettingsState()
    data class SavedFilter(val isSaved: Boolean = true) : FilterSettingsState()
}
