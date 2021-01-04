package br.com.eduardotanaka.warren.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.eduardotanaka.warren.data.model.api.LoginResponse
import br.com.eduardotanaka.warren.data.model.entity.base.ReflectsApiResponse
import java.io.Serializable

@Entity
data class Token(
    @PrimaryKey
    var accessToken: String = "",
    var refreshToken: String = ""
) : ReflectsApiResponse<Token, LoginResponse>, Serializable {
    override fun reflectFrom(apiResponse: LoginResponse): Token {
        accessToken = apiResponse.accessToken
        refreshToken = apiResponse.refreshToken
        return this
    }
}