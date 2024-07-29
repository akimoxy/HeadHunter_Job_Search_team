package ru.practicum.android.diploma.search.data.mapper

import ru.practicum.android.diploma.filter.domain.model.FilterGeneral
import ru.practicum.android.diploma.network.data.dto.linked.AreaDTO
import ru.practicum.android.diploma.network.data.dto.linked.BrandSnippet
import ru.practicum.android.diploma.network.data.dto.linked.Employer
import ru.practicum.android.diploma.network.data.dto.linked.LogoUrls
import ru.practicum.android.diploma.network.data.dto.linked.Salary
import ru.practicum.android.diploma.network.data.dto.linked.VacancyAtSearch
import ru.practicum.android.diploma.search.domain.model.AreaModel
import ru.practicum.android.diploma.search.domain.model.BrandSnippetModel
import ru.practicum.android.diploma.search.domain.model.EmployerModel
import ru.practicum.android.diploma.search.domain.model.LogoUrlsModel
import ru.practicum.android.diploma.search.domain.model.SalaryModel
import ru.practicum.android.diploma.search.domain.model.SearchParameters
import ru.practicum.android.diploma.search.domain.model.Vacancy

class SearchVacancyConverter {
    fun mapToVacancyModel(vacancy: VacancyAtSearch): Vacancy {
        return Vacancy(
            id = vacancy.id,
            name = vacancy.name,
            salary = mapToSalaryModel(vacancy.salary),
            employer = mapToEmployerModel(vacancy.employer),
            brandSnippet = mapToBrandSnippetModel(vacancy.brandSnippet),
            area = mapToAreaModel(vacancy.areaDTO)
        )
    }

    fun mapToListVacancyModel(vacancy: List<VacancyAtSearch>): List<Vacancy> {
        return vacancy.map { vacancyAtSearch ->
            mapToVacancyModel(vacancyAtSearch)
        }
    }

    fun mapToAreaModel(remote: AreaDTO?): AreaModel? {
        if (remote == null) return null
        return AreaModel(
            subAreas = remote.subAreaDTOS?.map { mapToAreaModel(it) },
            id = remote.id,
            name = remote.name,
            prepositional = remote.prepositional,
            parentId = remote.parentId,
        )

    }

    fun mapToSalaryModel(salaryDTO: Salary?): SalaryModel? {
        if (salaryDTO == null) return null
        return SalaryModel(
            salaryDTO.currency,
            salaryDTO.from,
            salaryDTO.gross,
            salaryDTO.to
        )
    }

    fun mapToBrandSnippetModel(brSnippet: BrandSnippet?): BrandSnippetModel? {
        if (brSnippet == null) return null
        return BrandSnippetModel(
            brSnippet.logo,
            brSnippet.logoXs,
            brSnippet.picture,
            brSnippet.pictureXs
        )
    }

    fun mapToEmployerModel(employerDTO: Employer?): EmployerModel? {
        if (employerDTO == null) return null
        return EmployerModel(
            id = employerDTO.id.orEmpty(),
            name = employerDTO.name,
            url = employerDTO.url.orEmpty(),
            vacanciesUrl = employerDTO.vacanciesUrl.orEmpty(),
            isTrusted = employerDTO.isTrusted,
            logoUrls = mapToLogosModel(employerDTO.logoUrls),
        )
    }

    fun mapToLogosModel(logosDTO: LogoUrls?): LogoUrlsModel? {
        if (logosDTO == null) return null
        return LogoUrlsModel(
            size90 = logosDTO.size90.orEmpty(),
            size240 = logosDTO.size240,
            raw = logosDTO.raw.orEmpty(),
        )
    }

    private fun checkAreaAndIndustryAbsence(filter: FilterGeneral): Boolean {
        return filter.area?.areaId.isNullOrEmpty() &&
            filter.country?.countryId.isNullOrEmpty() &&
            filter.industry?.industryId.isNullOrEmpty()
    }

    fun toSearchParameters(filter: FilterGeneral): SearchParameters? {
        if (
            !filter.hideNoSalaryItems!! &&
            checkAreaAndIndustryAbsence(filter) &&
            filter.expectedSalary.isNullOrEmpty()
        ) {
            return null
        } else {
            var areaToGo: String?
            var industryList: List<String>?
            with(filter) {
                areaToGo = if (area?.areaId.isNullOrEmpty()) country?.countryId else area?.areaId
                if (areaToGo == "") areaToGo = null
                industryList = if (industry?.industryId.isNullOrEmpty()) null else listOf(industry?.industryId!!)
                return SearchParameters(
                    areaId = areaToGo,
                    industryIds = industryList,
                    salary = expectedSalary?.toIntOrNull(),
                    withSalaryOnly = filter.hideNoSalaryItems,
                    page = null,
                    perPage = null,
                    currencyCode = null
                )
            }
        }
    }
}
