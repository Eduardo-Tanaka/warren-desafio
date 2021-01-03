package br.com.eduardotanaka.warren.data.api

import br.com.eduardotanaka.warren.data.model.Login
import br.com.eduardotanaka.warren.data.model.ObjetivoResponse
import br.com.eduardotanaka.warren.data.model.api.LoginResponse
import br.com.eduardotanaka.warren.data.model.entity.Token
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WarrenApi {

    @POST("/api/v2/account/login")
    suspend fun buscaToken(@Body login: Login): Response<LoginResponse>

    @GET("api/v2/portfolios/mine")
    suspend fun getObjetivos(): Call<ObjetivoResponse>
}