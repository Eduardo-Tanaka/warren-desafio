package br.com.eduardotanaka.warren.data.model.api

import br.com.eduardotanaka.warren.data.model.api.base.ApiResponseObject
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
) : ApiResponseObject