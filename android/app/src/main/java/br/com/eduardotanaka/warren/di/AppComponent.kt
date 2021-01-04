package br.com.eduardotanaka.warren.di

import br.com.eduardotanaka.warren.AppApplication
import android.app.Application
import br.com.eduardotanaka.warren.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ActivityBuilderModule::class,
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