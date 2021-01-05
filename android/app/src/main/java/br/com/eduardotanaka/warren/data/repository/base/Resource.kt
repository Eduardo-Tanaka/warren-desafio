package br.com.eduardotanaka.warren.data.repository.base

import br.com.eduardotanaka.warren.data.repository.helpers.DataFetchHelper
import retrofit2.Response
import java.io.IOException

/**
 * Um wrapper de recursos flexível para reunir e propagar propriedades associadas a ele
 * @param T O tipo de dado englobado
 */
class Resource<T> {
    /**
     * Os dados correspondentes ao recurso
     */
    var data: T? = null

    /**
     * Uma resposta para levar ao consumidor
     */
    var response: Response<out Any?>? = null

    /**
     * Uma exceção que pode ser consumida
     */
    var throwable: Throwable? = null

    /**
     * Descreve como este recurso foi obtido
     */
    var dataFetchStyle =
        DataFetchHelper.DataFetchStyle.NETWORK_FIRST_LOCAL_FAILOVER

    /**
     * Descreve o estilo de busca resultante
     * Permite ao consumidor especificar que os dados não são novos por causa da rede, etc.
     */
    var dataFetchStyleResult = DataFetchHelper.DataFetchStyle.Result.NO_FETCH

    /**
     * Se o recurso contém dados
     */
    fun hasData() = data != null

    /**
     * Se este recurso for considerado novo (em correspondência com [DataFetchHelper.DataFetchStyle])
     */
    var fresh: Boolean = false

    /**
     * Mensagem de erro local não segura para ser propagada
     */
    var errorMessage: String? = null

    /**
     * Se um erro de rede aconteceu durante a busca
     * Não significa necessariamente que dados "novos" não foram recebidos, consulte [DataFetchHelper.DataFetchStyle.LOCAL_FIRST_NETWORK_REFRESH_ALWAYS]
     */
    fun isNetworkIssue(): Boolean = throwable is IOException

    /**
     * Se um problema de API aconteceu durante a busca
     * Novamente, não significa necessariamente que dados "novos" não foram recebidos
     * por exemplo. 404 isNotFound(), 5XX Service Error, etc.
     */
    fun isApiIssue(): Boolean = !(response?.isSuccessful ?: true)

    /**
     * Copia o recurso para um novo tipo de dados (ou o mesmo)
     * @param newData S
     * @return Resource<S>
     */
    fun <S : Any?> copy(newData: S): Resource<S> {
        return Resource<S>().apply {
            data = newData
            response = this@Resource.response
            throwable = this@Resource.throwable
            dataFetchStyle = this@Resource.dataFetchStyle
            dataFetchStyleResult = this@Resource.dataFetchStyleResult
            fresh = this@Resource.fresh
            errorMessage = this@Resource.errorMessage
        }
    }
}