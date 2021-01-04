package br.com.eduardotanaka.warren.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.eduardotanaka.warren.di.ViewModelFactory
import br.com.eduardotanaka.warren.di.ViewModelKey
import br.com.eduardotanaka.warren.ui.login.LoginViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

/**
 * Usado para injetar dependencias nos ViewModels
 */
@Module
abstract class ViewModelModule {

    @Binds
    @Singleton
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModelImpl::class)
    abstract fun bindLoginViewModel(loginViewModelImpl: LoginViewModelImpl): ViewModel
}