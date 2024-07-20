package ru.practicum.android.diploma.filter.domain.impl

import ru.practicum.android.diploma.filter.domain.model.Country

interface CountryFilterRepository {
    fun getCountryId(): String

    fun getCountryName(): String

    fun saveCountry(country: Country)

    fun clearCountry()
}
