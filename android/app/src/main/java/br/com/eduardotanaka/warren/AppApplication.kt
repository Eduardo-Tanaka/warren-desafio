package br.com.eduardotanaka.warren

import br.com.eduardotanaka.warren.di.DaggerAppComponent
import dagger.android.DaggerApplication

class AppApplication : DaggerApplication() {

    private val appComponent = DaggerAppComponent.factory().create(this)
    override fun applicationInjector() = appComponent
}