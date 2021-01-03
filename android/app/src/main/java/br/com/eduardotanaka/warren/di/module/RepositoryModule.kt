package br.com.eduardotanaka.warren.di.module

import br.com.eduardotanaka.warren.data.repository.LoginRepository
import br.com.eduardotanaka.warren.data.repository.LoginRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Classes repository colocar aqui
 */
@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLoginRepository(loginRepository: LoginRepositoryImpl): LoginRepository
}