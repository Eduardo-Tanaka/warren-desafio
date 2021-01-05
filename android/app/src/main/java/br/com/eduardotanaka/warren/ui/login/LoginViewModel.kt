package br.com.eduardotanaka.warren.ui.login

import androidx.lifecycle.LiveData
import br.com.eduardotanaka.warren.data.model.Login
import br.com.eduardotanaka.warren.data.model.entity.Token
import br.com.eduardotanaka.warren.ui.base.StatefulResource

interface LoginViewModel {

    val token: LiveData<StatefulResource<Token?>>

    fun getToken(login: Login)

    fun verificaValidadeToken(): Boolean
}
