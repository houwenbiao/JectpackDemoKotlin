package com.qtimes.jetpackdemokotlin.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.qtimes.jetpackdemokotlin.common.MainApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class DataStoreUtil {

    companion object {

        private const val PreferencesDataStoreName: String = "JetPackPreferences"

        private val Context.dataStore by preferencesDataStore(PreferencesDataStoreName)


        fun setString(
            key: String,
            value: String,
            lifecycleScope: CoroutineScope = GlobalScope
        ) {
            val stringKey = stringPreferencesKey(key)
            lifecycleScope.launch(Dispatchers.IO) {
                MainApplication.context.dataStore.edit {
                    it[stringKey] = value
                }
            }
        }

        fun getString(key: String): Flow<String> {
            val stringKey = stringPreferencesKey(key)
            return MainApplication.context.dataStore.data.map {
                it[stringKey] ?: ""
            }
        }
    }
}