package br.com.eduardotanaka.warren.data.repository

import br.com.eduardotanaka.warren.data.model.Login
import br.com.eduardotanaka.warren.data.model.entity.Token
import br.com.eduardotanaka.warren.data.repository.base.Resource

interface LoginRepository {
    suspend fun getToken(login: Login): Resource<Token?>
}
