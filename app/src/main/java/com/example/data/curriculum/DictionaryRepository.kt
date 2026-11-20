package com.example.data.curriculum

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DictionaryEntry(
    val word: String,
    val partOfSpeech: String,
    val cefr: String,
    val ipa: String,
    val uzbek: String,
    val turkish: String,
    val russian: String,
    val example: String
)

class DictionaryRepository(private val context: Context) {
    @Volatile
    private var cachedEntries: List<DictionaryEntry>? = null

    suspend fun loadAll(): List<DictionaryEntry> = withContext(Dispatchers.IO) {
        cachedEntries?.let { return@withContext it }
        val entries = ArrayList<DictionaryEntry>(9000)
        context.assets.open("dictionary_7000.tsv").bufferedReader().useLines { lines ->
            var isFirst = true
            for (rawLine in lines) {
                if (rawLine.isBlank()) continue
                if (isFirst) {
                    isFirst = false
                    if (rawLine.startsWith("word\t")) continue
                }
                val cols = rawLine.split('\t')
                if (cols.size >= 8) {
                    entries.add(
                        DictionaryEntry(
                            word = cols[0].trim(),
                            partOfSpeech = cols[1].trim(),
                            cefr = cols[2].trim(),
                            ipa = cols[3].trim(),
                            uzbek = cols[4].trim(),
                            turkish = cols[5].trim(),
                            russian = cols[6].trim(),
                            example = cols[7].trim()
                        )
                    )
                }
            }
        }
        cachedEntries = entries
        entries
    }
}
