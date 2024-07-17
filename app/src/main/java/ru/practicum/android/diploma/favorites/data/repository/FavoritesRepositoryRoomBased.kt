package ru.practicum.android.diploma.favorites.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.db.data.db.AppDatabase
import ru.practicum.android.diploma.db.data.db.VacancyDbConvertor
import ru.practicum.android.diploma.details.domain.model.VacancyDetails
import ru.practicum.android.diploma.favorites.domain.impl.LocalRepository
import ru.practicum.android.diploma.utils.Resource
import java.io.UncheckedIOException

class FavoritesRepositoryRoomBased(
    private val appDatabase: AppDatabase,
    private val vacancyDbConvertor: VacancyDbConvertor
) : LocalRepository {
    override suspend fun getFavoritesVacancyList(): Flow<Resource<List<VacancyDetails>>> {
        return flow {
            try {
                val listVacancy = appDatabase.vacancyDao().getAllVacancy()
                emit(Resource.Success(listVacancy.map { vacancy ->
                    vacancyDbConvertor.mapVacancy(vacancy)
                }))
            } catch (e: UncheckedIOException) {
                emit(Resource.Error(message = e.message.toString()))
            }

        }
    }
}
// listOf(Mok.vacancyDetailsMok, Mok.vacancyDetailsMok)
