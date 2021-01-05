package br.com.eduardotanaka.warren.data.persistence.dao.base

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

abstract class BaseDao<T> {

    /**
     * Insira um objeto no banco de dados.
     *
     * @param obj o objeto a ser inserido.
     * @return The SQLite row id
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(obj: T): Long

    /**
     * Insire um array de objetos no banco de dados.
     *
     * @param obj os objetos a serem inseridos.
     * @return The SQLite row ids
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(obj: List<T>): List<Long>

    /**
     * Atualiza um objeto do banco de dados.
     *
     * @param obj o objeto a ser atualizado
     */
    @Update
    abstract fun update(obj: T)

    /**
     * Atualiza um array de objetos do banco de dados.
     *
     * @param obj lista de objetos a serem atualizados
     */
    @Update
    abstract fun update(obj: List<T>)

    /**
     * Deleta um objeto do banco de dados
     *
     * @param obj o objeto a ser deletado
     */
    @Delete
    abstract fun delete(obj: T)

    @Delete
    abstract fun delete(objs: List<T>)
}