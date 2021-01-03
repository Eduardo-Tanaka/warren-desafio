package br.com.eduardotanaka.warren.data.repository

import br.com.eduardotanaka.warren.data.api.WarrenApi
import br.com.eduardotanaka.warren.data.model.Login
import br.com.eduardotanaka.warren.data.model.api.LoginResponse
import br.com.eduardotanaka.warren.data.model.entity.Token
import br.com.eduardotanaka.warren.data.repository.base.BaseRepository
import br.com.eduardotanaka.warren.data.repository.base.Resource
import br.com.eduardotanaka.warren.data.repository.helpers.DataFetchHelper
import retrofit2.Response
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val warrenApi: WarrenApi,
) : BaseRepository(), LoginRepository {

    override suspend fun getToken(login: Login): Resource<Token?> {
        val dataFetchHelper = object : DataFetchHelper.NetworkOnly<Token?>(
            "LoginRepositoryImpl"
        ) {
            override suspend fun getDataFromNetwork(): Response<out Any?> {
                return warrenApi.buscaToken(login)
            }

            override suspend fun convertApiResponseToData(response: Response<out Any?>): Token? {
                return Token()
                    .reflectFrom(response.body() as LoginResponse)
            }

        }

        return dataFetchHelper.fetchDataIOAsync().await()
    }

}