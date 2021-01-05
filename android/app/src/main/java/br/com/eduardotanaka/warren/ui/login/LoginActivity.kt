package br.com.eduardotanaka.warren.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import br.com.eduardotanaka.warren.data.model.Login
import br.com.eduardotanaka.warren.ui.base.BaseActivity
import br.com.eduardotanaka.warren.ui.base.StatefulResource
import br.com.warren.challange.R

class LoginActivity : BaseActivity() {

    private val tag = LoginActivity::class.simpleName
    private val viewModel by viewModels<LoginViewModelImpl> { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tokenExpirado = viewModel.verificaValidadeToken()
        Log.d(tag, tokenExpirado.toString())

        // possui token válido pula a tela de login
        if (!tokenExpirado) {
            finish()
        }

        val login = Login(
            email = "mobile_test@warrenbrasil.com",
            password = "Warren123!"
        )
        viewModel.getToken(login)

        viewModel.token.observe(this, {
            if (it.state == StatefulResource.State.SUCCESS && it.hasData()) {
                Log.d(tag, it.resource?.data.toString())
            }
        })
    }
}