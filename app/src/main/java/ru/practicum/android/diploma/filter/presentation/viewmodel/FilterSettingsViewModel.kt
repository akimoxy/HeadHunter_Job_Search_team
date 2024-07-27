package ru.practicum.android.diploma.filter.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.domain.impl.FilterStorageRepository
import ru.practicum.android.diploma.filter.domain.model.AreaFilter
import ru.practicum.android.diploma.filter.domain.model.CountryFilter
import ru.practicum.android.diploma.filter.domain.model.FilterGeneral
import ru.practicum.android.diploma.filter.domain.model.Industry
import ru.practicum.android.diploma.filter.presentation.state.FilterSettingsState

class FilterSettingsViewModel(
    private val filterStorage: FilterStorageRepository
) : ViewModel() {

    private var jobStorage: Job? = null
    private var savedFilter: FilterGeneral = FilterGeneral()
    private val filterState = MutableLiveData<FilterSettingsState>()
    fun getState(): LiveData<FilterSettingsState> = filterState

    fun loadConfiguredFilterSettings() {
        jobStorage?.cancel()

        jobStorage = viewModelScope.launch(Dispatchers.IO) {
            savedFilter = getConfiguredFilterSettings()
            filterState.postValue(FilterSettingsState.Filter(savedFilter))
        }
    }

    fun loadSavedFilterSettings(isFirstLoad: Boolean) {
        if (!isFirstLoad) return

        jobStorage?.cancel()

        jobStorage = viewModelScope.launch(Dispatchers.IO) {
            if (filterStorage.isFilterActive()) {
                savedFilter = getSavedFilterSettings()
                savedFilterToConfigured(savedFilter)
            }
        }
    }

    fun saveFilterSettings() {
        jobStorage?.cancel()

        jobStorage = viewModelScope.launch(Dispatchers.IO) {
            filterStorage.saveAllFilterParameters(savedFilter)
            filterState.postValue(FilterSettingsState.SavedFilter())
        }
    }

    fun resetFilter() {
        filterStorage.clearAllFilterParameters()
    }

    fun resetFilterSettings() {
        jobStorage?.cancel()

        jobStorage = viewModelScope.launch(Dispatchers.IO) {
            resetFilter()
            filterState.postValue(FilterSettingsState.SavedFilter())
        }
    }

    private fun getConfiguredFilterSettings(): FilterGeneral {
        return filterStorage.getAllSavedParameters()
    }

    private fun getSavedFilterSettings(): FilterGeneral {
        return filterStorage.getAllFilterParameters()
    }

    private fun savedFilterToConfigured(filter: FilterGeneral) {
        if (filter.area != null) {
            filterStorage.saveArea(
                AreaFilter(
                    areaId = filter.area.areaId.toString(),
                    areaName = filter.area.areaName.toString()
                )
            )
        } else {
            resetArea()
        }
        if (filter.country != null) {
            filterStorage.saveCountry(
                CountryFilter(
                    countryId = filter.country.countryId.toString(),
                    countryName = filter.country.countryName.toString()
                )
            )
        } else {
            resetCountry()
        }

        if (filter.industry != null) {
            filterStorage.saveIndustry(
                Industry(
                    id = filter.industry.industryId.toString(),
                    industries = emptyList(),
                    name = filter.industry.industryName.toString()
                )
            )
        } else {
            resetIndustry()
        }

        filterStorage.saveHideNoSalaryItems(filter.hideNoSalaryItems)
        if (filter.expectedSalary != null) {
            filterStorage.saveExpectedSalary(filter.expectedSalary.toString())
        } else {
            resetSalary()
        }
    }

    fun changeSalary(newSalary: String) {
        savedFilter = savedFilter.copy(expectedSalary = newSalary)
    }

    fun changeHideNoSalary(noSalary: Boolean) {
        savedFilter = savedFilter.copy(hideNoSalaryItems = noSalary)
    }

    private fun resetSalary() {
        savedFilter = savedFilter.copy(expectedSalary = String())
    }

    fun resetRegion() {
        resetArea()
        resetCountry()
        loadConfiguredFilterSettings()
    }

    private fun resetArea() {
        filterStorage.saveArea(
            AreaFilter(
                areaId = String(),
                areaName = String()
            )
        )
    }

    private fun resetCountry() {
        filterStorage.saveCountry(
            CountryFilter(
                countryId = String(),
                countryName = String()
            )
        )
    }

    fun resetIndustry() {
        filterStorage.saveIndustry(
            Industry(
                id = String(),
                industries = emptyList(),
                name = String()
            )
        )
        loadConfiguredFilterSettings()
    }
}
