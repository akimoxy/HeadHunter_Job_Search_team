package ru.practicum.android.diploma.db.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class VacancyInfoEntity(
    @Embedded
    val vacancy: VacancyEntity,

    @Relation(
        parentColumn = "areaId",
        entityColumn = "id"
    )
    val areaList: List<AreaEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "vacancyId"
    )
    val brandSnippetList: List<BrandSnippetEntity>,

    @Relation(
        parentColumn = "countryId",
        entityColumn = "id"
    )
    val countryList: List<CountryEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "vacancyId"
    )
    val logoList: List<EmployerJoinLogo>,

    @Relation(
        parentColumn = "experienceId",
        entityColumn = "id"
    )
    val experienceList: List<ExperienceEntity>,

    @Relation(
        parentColumn = "industryId",
        entityColumn = "id"
    )
    val industryList: List<IndustryJoinSphereEntity>,

    @Relation(
        parentColumn = "salaryId",
        entityColumn = "id"
    )
    val salaryList: List<SalaryJoinCurrency>,

    @Relation(
        parentColumn = "vacancyFuncTitleId",
        entityColumn = "id"
    )
    val positionList: List<VacancyFuncTitleEntity>
)
