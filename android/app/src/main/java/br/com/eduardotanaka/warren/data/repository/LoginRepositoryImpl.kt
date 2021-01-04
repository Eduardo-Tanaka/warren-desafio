package br.com.eduardotanaka.warren.data.repository

import android.content.SharedPreferences
import br.com.eduardotanaka.warren.data.api.WarrenApi
import br.com.eduardotanaka.warren.data.model.Login
import br.com.eduardotanaka.warren.data.model.api.LoginResponse
import br.com.eduardotanaka.warren.data.model.entity.Token
import br.com.eduardotanaka.warren.data.persistence.AppDatabase
import br.com.eduardotanaka.warren.data.repository.base.BaseRepository
import br.com.eduardotanaka.warren.data.repository.base.Resource
import br.com.eduardotanaka.warren.data.repository.helpers.DataFetchHelper
import retrofit2.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val warrenApi: WarrenApi,
    private val appDatabase: AppDatabase,
    private val sharedPreferences: SharedPreferences
) : BaseRepository(), LoginRepository {

    override suspend fun getToken(login: Login): Resource<Token?> {
        val dataFetchHelper = object : DataFetchHelper.LocalFirstUntilStale<Token?>(
            "LoginRepositoryImpl",
            sharedPreferences,
            "0000CacheKey.STOCK.toString()",
            "",
            TimeUnit.HOURS.toSeconds(24) //storing information for a day (doesn't change much)
        ) {
            override suspend fun getDataFromNetwork(): Response<out Any?> {
                return warrenApi.buscaToken(login)
            }

            override suspend fun convertApiResponseToData(response: Response<out Any?>): Token? {
                return Token()
                    .reflectFrom(response.body() as LoginResponse)
            }

            override suspend fun getDataFromLocal(): Token? {
                return Token("s", "ss")
            }

            override suspend fun storeFreshDataToLocal(data: Token?): Boolean {
                data?.let {
                    //appDatabase.
                    return true
                } ?: run {
                    return false
                }
            }
        }

        return dataFetchHelper.fetchDataIOAsync().await()
    }

}