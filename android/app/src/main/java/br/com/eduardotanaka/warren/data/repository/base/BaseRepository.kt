package br.com.eduardotanaka.warren.data.repository.base

import kotlinx.coroutines.Dispatchers

abstract class BaseRepository {
    var ioDispatcher = Dispatchers.IO
}