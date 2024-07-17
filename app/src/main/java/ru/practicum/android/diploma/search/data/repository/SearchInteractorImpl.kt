package ru.practicum.android.diploma.search.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.search.data.mapper.SearchVacancyConverter
import ru.practicum.android.diploma.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.search.domain.model.VacancyListResult
import ru.practicum.android.diploma.utils.Resource

class SearchInteractorImpl(val repository: SearchRepository, val converter: SearchVacancyConverter) : SearchInteractor {
    override fun searchVacancy(expression: String): Flow<VacancyListResult> = flow {
        repository
            .searchVacancy(expression)
            .collect { vacancyListResponse ->
                when (vacancyListResponse) {
                    is Resource.Success -> {
                        val vacancyList =
                            converter.mapToListVacancyModel(vacancyListResponse.data!!.vacancyAtSearchList)
                        var foundVacancy = vacancyListResponse.data.totalFound
                        if (foundVacancy == null) {
                            foundVacancy = -1
                        }
                        val pagesTotal = vacancyListResponse.data.totalPages
                        Log.d("тотал фаунд", foundVacancy.toString())
                        Log.d("тотал пэйджз", pagesTotal.toString())
                        emit(
                            VacancyListResult(
                                result = vacancyList,
                                errorMessage = "",
                                foundVacancy = foundVacancy.toInt(),
                                page = 0,
                                pages = pagesTotal.toInt(),

                                )
                        )
                    }

                    is Resource.Error -> {
                        emit(
                            VacancyListResult(emptyList(), vacancyListResponse.message, 0, 0, 0)
                        )
                    }
                }
            }
    }
}
