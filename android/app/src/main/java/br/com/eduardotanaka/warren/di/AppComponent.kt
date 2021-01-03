package br.com.eduardotanaka.warren.di

import br.com.eduardotanaka.warren.AppApplication
import android.app.Application
import br.com.eduardotanaka.warren.di.module.AppModule
import br.com.eduardotanaka.warren.di.module.NetworkModule
import br.com.eduardotanaka.warren.di.module.PersistenceModule
import br.com.eduardotanaka.warren.di.module.RepositoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        NetworkModule::class,
        PersistenceModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent : AndroidInjector<AppApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

}