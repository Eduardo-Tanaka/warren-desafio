package br.com.eduardotanaka.warren.util

import android.content.SharedPreferences
import org.threeten.bp.OffsetDateTime

object RepositoryUtil {

    fun getSecondsSinceEpoch() = OffsetDateTime.now().toEpochSecond()

    /**
     *
     * @param cacheKey String - Uma string exclusiva para representar o cache
     * @param keyDescriptor String - Uma string para dar uma descrição secundária
     * @param cacheLengthSeconds Long - Por quanto tempo o cache é considerado novo (use TimeUnit. [MINUTES / HOURS / DAYS] .toSeconds (x))
     * @return Boolean
     */
    fun isCacheStale(
        sharedPreferences: SharedPreferences,
        cacheKey: String,
        keyDescriptor: String? = "",
        cacheLengthSeconds: Long
    ): Boolean {
        val lastCacheCurrentSeconds =
            sharedPreferences.getLong(cacheKey.toString() + "_" + keyDescriptor, -1L)
        if (lastCacheCurrentSeconds == -1L) return true
        return (getSecondsSinceEpoch().minus(lastCacheCurrentSeconds)) > cacheLengthSeconds
    }

    fun resetCache(
        sharedPreferences: SharedPreferences,
        cacheKey: String,
        keyDescriptor: String? = ""
    ) {
        sharedPreferences
            .edit()
            .putLong(
                cacheKey + "_" + keyDescriptor,
                getSecondsSinceEpoch()
            )
            .apply()
    }
}