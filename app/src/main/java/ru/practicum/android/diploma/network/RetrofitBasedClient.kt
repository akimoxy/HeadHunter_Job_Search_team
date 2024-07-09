package ru.practicum.android.diploma.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import ru.practicum.android.diploma.network.api.HeadHunterApplicationApi
import ru.practicum.android.diploma.network.api.HeadHunterNetworkClient
import ru.practicum.android.diploma.network.dto.HeadHunterRequest
import ru.practicum.android.diploma.network.dto.responses.AreasResponse
import ru.practicum.android.diploma.network.dto.responses.CountriesResponse
import ru.practicum.android.diploma.network.dto.responses.IndustryResponse
import ru.practicum.android.diploma.network.dto.responses.LocalesResponse
import ru.practicum.android.diploma.network.dto.responses.Response
import ru.practicum.android.diploma.network.dto.responses.SkillSuggestionsResponse
import java.io.UncheckedIOException

class RetrofitBasedClient(retrofit: Retrofit) : HeadHunterNetworkClient {
    private val serverService = retrofit.create(HeadHunterApplicationApi::class.java)
    override suspend fun doRequest(request: HeadHunterRequest): Response {
        // if (request !is HeadHunterRequest) return Response().apply { resultCode = -1 }
        return withContext(Dispatchers.IO) {
            try {
                val response = when (request) {
                    HeadHunterRequest.Locales -> LocalesResponse(localeList = serverService.getLocales())
                    HeadHunterRequest.Dictionaries -> serverService.getDictionaries()
                    HeadHunterRequest.Industries -> IndustryResponse(industriesList = serverService.getIndustries())
                    HeadHunterRequest.Areas -> AreasResponse(areasList = serverService.getAreas())
                    HeadHunterRequest.Counties -> CountriesResponse(countriesList = serverService.getCountries())
                    is HeadHunterRequest.SkillsSuggestions -> SkillSuggestionsResponse(
                        skillsList = serverService.getSkillSuggestions(request.textForSuggestions).skillsList
                    )
                }
                response.apply { resultCode = Response.SUCCESS }
            } catch (e: UncheckedIOException) {
                Response().apply {
                    errorMessage = e.message
                    resultCode = Response.FAILURE
                }
            }
        }
    }
}
