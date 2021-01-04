package br.com.eduardotanaka.warren

import br.com.eduardotanaka.warren.di.DaggerAppComponent
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.DaggerApplication

class AppApplication : DaggerApplication() {

    private val appComponent = DaggerAppComponent.factory().create(this)
    override fun applicationInjector() = appComponent

    override fun onCreate() {
        super.onCreate()

        // Threeten DateTime initialization
        AndroidThreeTen.init(this)
    }
}