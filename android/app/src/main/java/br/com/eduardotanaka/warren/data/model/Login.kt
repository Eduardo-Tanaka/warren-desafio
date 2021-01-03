package br.com.eduardotanaka.warren.data.model

import java.io.Serializable

data class Login(
    val email: String,
    val password: String
) : Serializable