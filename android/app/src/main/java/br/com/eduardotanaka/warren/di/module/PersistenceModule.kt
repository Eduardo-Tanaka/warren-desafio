package br.com.eduardotanaka.warren.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import br.com.eduardotanaka.warren.data.persistence.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * configurações de banco e SharedPreferences
 */
@Module
class PersistenceModule {

    companion object {
        private const val WARREN_DATABASE_NAME = "warren.db"
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(applicationContext: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(applicationContext)

    @Singleton
    @Provides
    fun provideAppDatabase(applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext.applicationContext,
            AppDatabase::class.java,
            WARREN_DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}