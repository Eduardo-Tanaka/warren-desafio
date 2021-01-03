package br.com.eduardotanaka.warren.data.api

import br.com.eduardotanaka.warren.data.model.Login
import br.com.eduardotanaka.warren.data.model.ObjetivoResponse
import br.com.eduardotanaka.warren.data.model.Token
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WarrenApi {

    @POST("/api/v2/account/login")
    suspend fun buscaToken(@Body login: Login): Call<Token>

    @GET("api/v2/portfolios/mine")
    suspend fun buscaToken(): Call<ObjetivoResponse>
}