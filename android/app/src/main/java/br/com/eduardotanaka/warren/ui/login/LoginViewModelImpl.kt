package br.com.eduardotanaka.warren.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.eduardotanaka.warren.data.model.Login
import br.com.eduardotanaka.warren.data.model.entity.Token
import br.com.eduardotanaka.warren.data.repository.LoginRepositoryImpl
import br.com.eduardotanaka.warren.ui.base.BaseViewModel
import br.com.eduardotanaka.warren.ui.base.StatefulResource
import br.com.warren.challange.R
import com.auth0.android.jwt.JWT
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class LoginViewModelImpl
@Inject constructor(
    private val loginRepositoryImpl: LoginRepositoryImpl,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel(), LoginViewModel {

    private val mutableToken: MutableLiveData<StatefulResource<Token?>> = MutableLiveData()
    override val token: LiveData<StatefulResource<Token?>> = mutableToken

    override fun getToken(login: Login) {
        launch {
            mutableToken.value = StatefulResource.with(StatefulResource.State.LOADING)
            val resource = loginRepositoryImpl.getToken(login)
            when {
                resource.hasData() -> {
                    //return the value
                    mutableToken.value = StatefulResource.success(resource)
                }
                resource.isNetworkIssue() -> {
                    mutableToken.value = StatefulResource<Token?>()
                        .apply {
                            setMessage(R.string.no_network_connection)
                            setState(StatefulResource.State.ERROR_NETWORK)
                        }
                }
                resource.isApiIssue() -> //TODO 4xx isn't necessarily a service error, expand this to sniff http code before saying service error
                    mutableToken.value = StatefulResource<Token?>()
                        .apply {
                            setState(StatefulResource.State.ERROR_API)
                            setMessage(R.string.service_error)
                        }
                else -> mutableToken.value = StatefulResource<Token?>()
                    .apply {
                        setState(StatefulResource.State.SUCCESS)
                        setMessage(R.string.token_not_found)
                    }
            }
        }
    }

    override fun verificaValidadeToken() : Boolean {
        val token = sharedPreferences.getString("TOKEN", "")
        if (token.equals("")) {
            return true
        }

        val jwt = JWT(token!!)

        // adiciona vários segundos de margem para a verificação retornar false já que o token gerado sempre vem com ext: 1571260104 (16/10/2019)
        return jwt.isExpired(1000000000000000000); // segundos de margem
    }
}