package ru.practicum.android.diploma.filter.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.domain.api.PlaceToWorkFilterInteractor
import ru.practicum.android.diploma.filter.domain.model.AreaFilter
import ru.practicum.android.diploma.filter.domain.model.CountryFilter
import ru.practicum.android.diploma.filter.presentation.state.PlaceToWorkFilterState

class PlaceToWorkFilterViewModel(private val placeToWorkFilterInteractor: PlaceToWorkFilterInteractor) : ViewModel() {
    private val _stateLiveData = MutableLiveData<PlaceToWorkFilterState>()
    val stateLiveData: LiveData<PlaceToWorkFilterState> = _stateLiveData

    fun saveFilterAreaParameters() {
        val currentState = _stateLiveData.value as PlaceToWorkFilterState.AreaFilter
        placeToWorkFilterInteractor.saveCountry(
            countryId = currentState.countryId,
            countryName = currentState.countryName
        )
        placeToWorkFilterInteractor.saveArea(
            areaId = currentState.areaId,
            areaName = currentState.areaName
        )
    }

    fun clearCountry() {
        placeToWorkFilterInteractor.clearCountry()
        updateCurrentFilterAreaParameters()
    }

    fun clearArea() {
        placeToWorkFilterInteractor.clearArea()
        updateCurrentFilterAreaParameters()
    }

    fun getCurrentFilterAreaParameters() {
        viewModelScope.launch {
            val currentCountry = placeToWorkFilterInteractor.getCurrentCountryChoice()
            val currentArea = placeToWorkFilterInteractor.getCurrentAreaChoice()

            if (!currentArea?.areaName.isNullOrEmpty() && currentCountry?.countryName.isNullOrEmpty()) {
                val parentOfRegionById =
                    placeToWorkFilterInteractor.getCountryForRegion(currentArea?.areaId!!)
                setLiveDataValue(parentOfRegionById, currentArea)
                placeToWorkFilterInteractor.saveCountry(
                    countryId = parentOfRegionById?.countryId,
                    countryName = parentOfRegionById?.countryName
                )
            } else {
                setLiveDataValue(currentCountry, currentArea)
            }
        }
    }

    fun updateCurrentFilterAreaParameters() {
        val currentCountry = placeToWorkFilterInteractor.getCurrentCountryChoice()
        val currentArea = placeToWorkFilterInteractor.getCurrentAreaChoice()
        setLiveDataValue(currentCountry, currentArea)
    }

    private fun setLiveDataValue(currentCountry: CountryFilter?, currentArea: AreaFilter?) {
        _stateLiveData.value = PlaceToWorkFilterState.AreaFilter(
            countryId = currentCountry?.countryId,
            countryName = currentCountry?.countryName,
            areaId = currentArea?.areaId,
            areaName = currentArea?.areaName
        )
    }
}
