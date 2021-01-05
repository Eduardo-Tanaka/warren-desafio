package br.com.eduardotanaka.warren.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.eduardotanaka.warren.data.model.entity.Token
import br.com.eduardotanaka.warren.data.persistence.dao.TokenDao

@Database(
    version = 1,
    entities = [
        Token::class
    ],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tokenDao(): TokenDao

}
