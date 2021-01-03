package br.com.eduardotanaka.warren.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [

    ]
)
abstract class AppDatabase : RoomDatabase() {

}
