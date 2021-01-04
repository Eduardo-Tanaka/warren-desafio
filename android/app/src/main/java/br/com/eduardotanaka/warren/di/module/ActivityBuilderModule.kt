package br.com.eduardotanaka.warren.di.module

import br.com.eduardotanaka.warren.ui.login.LoginActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Colocar as activities aqui e caso necessário importar os modules
 */
@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributesLoginActivity(): LoginActivity
}