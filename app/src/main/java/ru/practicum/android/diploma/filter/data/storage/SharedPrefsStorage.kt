package ru.practicum.android.diploma.filter.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import ru.practicum.android.diploma.filter.domain.model.Filter

const val FILTER_KEY = "saved_filter"

class SharedPrefsStorage(private val sharedPreferences: SharedPreferences, private val gson: Gson) : FilterStorage {
    override fun saveFilter(filter: Filter) {
        val json = gson.toJson(filter)
        sharedPreferences.edit().putString(FILTER_KEY, json).apply()
    }

    override fun getFilter(): Filter {
        val json = sharedPreferences.getString(FILTER_KEY, null)
            ?: return Filter()
        return gson.fromJson(json, Filter::class.java)
    }

}