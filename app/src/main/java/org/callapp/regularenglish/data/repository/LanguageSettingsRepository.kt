package org.callapp.regularenglish.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.callapp.regularenglish.data.local.dao.LanguageSettingsDao
import org.callapp.regularenglish.data.local.entity.LanguageSettingsEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageSettingsRepository @Inject constructor(private val dao: LanguageSettingsDao) {
    fun observe(): Flow<LanguageSettingsEntity?> = dao.observe()
    suspend fun current(): String = withContext(Dispatchers.IO) { dao.get()?.language ?: "vi" }
    suspend fun set(language: String) = withContext(Dispatchers.IO) { dao.upsert(LanguageSettingsEntity(language = language)) }
    companion object { val supported = listOf("fr", "pt", "ja", "vi", "ko", "ru", "zh", "de", "es") }
}
