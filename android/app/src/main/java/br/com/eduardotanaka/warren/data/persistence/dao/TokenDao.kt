package br.com.eduardotanaka.warren.data.persistence.dao

import androidx.room.Dao
import br.com.eduardotanaka.warren.data.model.entity.Token
import br.com.eduardotanaka.warren.data.persistence.dao.base.BaseDao

@Dao
abstract class TokenDao : BaseDao<Token>() {
}