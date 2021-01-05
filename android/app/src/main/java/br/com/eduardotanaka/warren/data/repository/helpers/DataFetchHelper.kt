package br.com.eduardotanaka.warren.data.repository.helpers

import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.WorkerThread
import br.com.eduardotanaka.warren.data.repository.base.Resource
import br.com.eduardotanaka.warren.data.repository.helpers.DataFetchHelper.DataFetchStyle
import br.com.eduardotanaka.warren.data.repository.helpers.DataFetchHelper.DataFetchStyle.Result
import br.com.eduardotanaka.warren.util.RepositoryUtil
import br.com.eduardotanaka.warren.util.onMainThread
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.Response


/**
 * Esta classe auxiliar encapsula alguns dos estilos comuns de busca de dados (definidos aqui [DataFetchStyle])
 *
 * Dependendo do estilo escolhido, alguns ou todos os métodos podem ser necessários para que a funcionalidade funcione corretamente
 *
 * [DataFetchHelper.fetchDataAsync] retorna um assíncrono adiado que deve ser consumido na camada do modelo de visualização
 *
 * @param T O tipo de dados que esta classe está buscando
 * @property tag String Um nome para o buscador por motivos de log
 * @property dataFetchStyle DataFetchStyle? O estilo que este coletor deve respeitar
 * @property sharedPreferences SharedPreferences? Obrigatório para estilos de cache local
 * @property cacheKey CacheKey? Uma chave para descrever seu cache na memória, apenas para estilos de cache local
 * @property cacheDescriptor String? Uma descrição da chave (por exemplo, nem todos os usuarios devem compartilhar o mesmo cache,
 * então use o nome do usuarios como um descritor)
 * @property cacheLengthSeconds Long Por quanto tempo o cache deve ser armazenado, apenas para estilos de cache local
 *
 */
abstract class DataFetchHelper<T>(
    val tag: String,
    private val dataFetchStyle: DataFetchStyle? = DataFetchStyle.LOCAL_FIRST_NETWORK_REFRESH_ALWAYS,
    private val sharedPreferences: SharedPreferences? = null,
    private val cacheKey: String? = null,
    private val cacheDescriptor: String? = "",
    private val cacheLengthSeconds: Long = 0L
) {

    /**
     * Descreve a abordagem de uma chamada de dados específica para buscar dados
     * O [Resource] resultante conterá um [Result] correspondente
     * que descreve o resultado da busca
     */
    enum class DataFetchStyle {

        /**
         * DEFAULT
         *
         *
         * Busca no armazenamento local,
         * mas sempre acompanhe com uma chamada de rede para armazenar dados atualizados
         *
         * Pros: Depende dos dados locais, sempre atualiza em segundo plano
         * Cons: Maximizando as chamadas de rede, a chamada de rede deve terminar antes que os dados locais sejam retornados
         * TODO retornar dados locais imediatamente, atualizar de forma assíncrona
         */
        LOCAL_FIRST_NETWORK_REFRESH_ALWAYS,

        /**
         * Busca do armazenamento local até que esteja obsoleto (por exemplo, cache expirado) e puxe da rede para restaurar
         *
         * Pros: Cache flexível, permitindo minimizar chamadas de rede,
         * deve ser instantâneo na maioria das vezes, failover de novas chamadas de rede
         * para armazenamento local.
         * Cons: Dados recentes não são óbvios para os usuários finais
         */
        LOCAL_FIRST_UNTIL_STALE,

        /**
         *
         * Tenta a rede primeiro, se for ruim / nenhuma rede, faça failover para o armazenamento local
         *
         * Pros: Dependente de falha, dados sempre atualizados quando possível
         * Cons: Maximizando chamadas de rede por solicitação
         */
        NETWORK_FIRST_LOCAL_FAILOVER,

        /**
         * Buscar apenas na rede
         *
         * Pros: Não é necessário armazenamento local, dados sempre atualizados quando possível
         * Cons: Nenhum mecanismo de falha, maximizando as chamadas de rede por solicitação
         */
        NETWORK_ONLY,

        /**
         * Buscar apenas no armazenamento local (db, sharedprefs, etc.)
         *
         * Pros: Nenhuma rede necessária, virtualmente instantânea
         * Cons: Sem capacidade de atualização no servidor remoto (pode não ser um problema)
         */
        LOCAL_ONLY;

        /**
         * Descreve como os dados foram obtidos, em correspondência com o
         * busca estilo see [DataFetchStyle]
         */
        enum class Result {
            /**
             * DEFAULT
             *
             * Nenhum dado foi buscado devido a falha de rede, falha local ou ambos.
             *
             * Isso também pode ser usado quando [DataFetchStyle.LOCAL_ONLY] é especificado
             * e não há dados locais
             */
            NO_FETCH,

            /**
             * Obtido do local porque a rede falhou
             * [DataFetchStyle.NETWORK_FIRST_LOCAL_FAILOVER]
             */
            LOCAL_DATA_NETWORK_FAIL,

            /**
             * Obtido em local fresco
             * [DataFetchStyle.LOCAL_FIRST_UNTIL_STALE]
             */
            LOCAL_DATA_FRESH,

            /**
             * Obtido do local
             * [DataFetchStyle.LOCAL_FIRST_NETWORK_REFRESH_ALWAYS]
             */
            LOCAL_DATA_FIRST,

            /**
             * Obtido do local porque essa é a única fonte de onde ele pode extrair
             * [DataFetchStyle.LOCAL_ONLY]
             */
            LOCAL_DATA_ONLY,

            /**
             * Obtido da rede porque essa é a única fonte de onde ele pode extrair
             * [DataFetchStyle.NETWORK_ONLY]
             */
            NETWORK_DATA_ONLY,

            /**
             * Obtido da rede porque uma conexão estava ativa
             * [DataFetchStyle.NETWORK_FIRST_LOCAL_FAILOVER]
             */
            NETWORK_DATA_FIRST,

            /**
             * Obtido da rede porque o local estava desatualizado
             * [DataFetchStyle.LOCAL_FIRST_UNTIL_STALE]
             */
            NETWORK_DATA_LOCAL_STALE,

            /**
             * Obtido da rede porque o local ainda não foi preenchido
             */
            NETWORK_DATA_LOCAL_MISSING
        }
    }

    /**
     * Classes internas para complementar estilos específicos
     */
    abstract class LocalOnly<S>(
        tag: String
    ) : DataFetchHelper<S>(
        tag,
        DataFetchStyle.LOCAL_ONLY
    ) {
        abstract override suspend fun getDataFromLocal(): S
    }

    abstract class NetworkOnly<S>(
        tag: String
    ) : DataFetchHelper<S>(
        tag,
        DataFetchStyle.NETWORK_ONLY
    ) {
        abstract override suspend fun getDataFromNetwork(): Response<out Any?>
        abstract override suspend fun convertApiResponseToData(response: Response<out Any?>): S
    }

    abstract class LocalFirstNetworkRefreshAlways<S>(
        tag: String,
        sharedPreferences: SharedPreferences,
        cacheKey: String,
        cacheDescriptor: String,
        cacheLengthSeconds: Long
    ) : DataFetchHelper<S>(
        tag,
        DataFetchStyle.LOCAL_FIRST_NETWORK_REFRESH_ALWAYS,
        sharedPreferences,
        cacheKey,
        cacheDescriptor,
        cacheLengthSeconds
    ) {
        abstract override suspend fun getDataFromLocal(): S?
        abstract override suspend fun getDataFromNetwork(): Response<out Any?>
        abstract override suspend fun convertApiResponseToData(response: Response<out Any?>): S
        abstract override suspend fun storeFreshDataToLocal(data: S): Boolean
    }

    abstract class LocalFirstUntilStale<S>(
        tag: String,
        sharedPreferences: SharedPreferences,
        cacheKey: String,
        cacheDescriptor: String,
        cacheLengthSeconds: Long
    ) : DataFetchHelper<S>(
        tag,
        DataFetchStyle.LOCAL_FIRST_UNTIL_STALE,
        sharedPreferences,
        cacheKey,
        cacheDescriptor,
        cacheLengthSeconds
    ) {
        abstract override suspend fun getDataFromLocal(): S?
        abstract override suspend fun getDataFromNetwork(): Response<out Any?>
        abstract override suspend fun convertApiResponseToData(response: Response<out Any?>): S
        abstract override suspend fun storeFreshDataToLocal(data: S): Boolean
    }

    abstract class NetworkFirstLocalFailover<S>(
        tag: String
    ) : DataFetchHelper<S>(
        tag,
        DataFetchStyle.NETWORK_FIRST_LOCAL_FAILOVER
    ) {
        abstract override suspend fun getDataFromLocal(): S
        abstract override suspend fun getDataFromNetwork(): Response<out Any?>
        abstract override suspend fun convertApiResponseToData(response: Response<out Any?>): S
        abstract override suspend fun storeFreshDataToLocal(data: S): Boolean
    }

    /**
     * Buscar dados de um recurso local
     * @return T?
     */
    @WorkerThread
    open suspend fun getDataFromLocal(): T? {
        throw NotImplementedError("getDataFromLocal should be implemented to support $dataFetchStyle")
    }

    /**
     * @return Response<out Any> - Como o modelo que estamos buscando pode não corresponder à resposta da API, out Any
     * see: [ReflectsApiResponse]
     */
    @WorkerThread
    open suspend fun getDataFromNetwork(): Response<out Any?> {
        throw NotImplementedError("getDataFromNetwork should be implemented to support $dataFetchStyle")
    }

    /**
     * Opcionalmente, forneça uma conversão de uma resposta da API para o tipo necessário
     *
     * see: [ReflectsApiResponse]
     * @param response Response<Any>
     * @return T
     */
    open suspend fun convertApiResponseToData(response: Response<out Any?>): T {
        try {
            return response.body() as T
        } catch (e: Exception) {
            throw ClassCastException(
                "$e - Cannot convert ${response.body()!!::class.java.simpleName} to Data Fetch type, " +
                        "override this method to provide conversion."
            )
        }
    }

    /**
     * Armazena os dados para recuperação futura
     * @param data T
     * @return Se os dados foram armazenados ou não
     */
    open suspend fun storeFreshDataToLocal(data: T): Boolean {
        throw NotImplementedError("storeFreshDataToLocal should be implemented to support $dataFetchStyle")
    }

    /**
     * Execute uma operação depois que os dados forem buscados
     * por exemplo. Atualize o registro histórico após uma busca bem-sucedida
     */
    open suspend fun operateOnDataPostFetch(data: T) {
        //Optional
    }

    /**
     * Busca o recurso imediatamente, em uma co-rotina gerenciada fora desta classe
     *
     * Deve estar em um worker thread (IO Dispatcher) se uma rede e / ou transação de dados local ocorrer
     * @return Resource<T>
     */
    suspend fun fetchData(): Resource<T> {
        return fetchDataByStyle(sharedPreferences, cacheKey)
    }

    /**
     * Receber um valor adiado, garantias de não estar no thread principal
     * @return Deferred<Resource<T>>
     */
    suspend fun fetchDataIOAsync(): Deferred<Resource<T>> = withContext(Dispatchers.IO) {
        async { fetchDataByStyle(sharedPreferences, cacheKey) }
    }

    private suspend fun fetchDataByStyle(
        sharedPreferences: SharedPreferences?,
        cacheKey: String?
    ): Resource<T> {
        val resource = Resource<T>()
        resource.dataFetchStyleResult = Result.NO_FETCH
        resource.dataFetchStyle = dataFetchStyle ?: DataFetchStyle.NETWORK_FIRST_LOCAL_FAILOVER

        if (onMainThread()) {
            throw IllegalThreadStateException("Cannot perform Network nor Local storage transactions on main thread!")
        }

        when (dataFetchStyle) {
            DataFetchStyle.NETWORK_FIRST_LOCAL_FAILOVER -> {
                resource.data = refreshDataFromNetwork(
                    resource,
                    DataFetchStyle.NETWORK_FIRST_LOCAL_FAILOVER
                )
                if (resource.data == null) {
                    log("Unable to get data from network, failing over to local")
                    resource.data = getDataFromLocal()
                    resource.fresh = false
                    resource.dataFetchStyleResult = Result.LOCAL_DATA_NETWORK_FAIL
                } else {
                    resource.fresh = true
                    resource.dataFetchStyleResult = Result.NETWORK_DATA_FIRST
                }
            }
            DataFetchStyle.NETWORK_ONLY -> {
                resource.data = refreshDataFromNetwork(resource, DataFetchStyle.NETWORK_ONLY)
                resource.fresh = true
                resource.dataFetchStyleResult = Result.NETWORK_DATA_ONLY
            }
            DataFetchStyle.LOCAL_ONLY -> {
                resource.data = getDataFromLocal()
                resource.fresh = true
                resource.dataFetchStyleResult = Result.LOCAL_DATA_ONLY
            }
            DataFetchStyle.LOCAL_FIRST_NETWORK_REFRESH_ALWAYS -> {
                resource.data = getDataFromLocal()
                //TODO:
                //always refreshing following it
                val dataFromNetwork =
                    refreshDataFromNetwork(
                        resource,
                        DataFetchStyle.LOCAL_FIRST_NETWORK_REFRESH_ALWAYS
                    )
                if (resource.data == null) {
                    log("Local data was empty, so returning data from network first")
                    resource.data = dataFromNetwork
                    resource.dataFetchStyleResult = Result.NETWORK_DATA_LOCAL_MISSING
                } else {
                    log("Returning local data first, refreshing in background")
                    resource.dataFetchStyleResult = Result.LOCAL_DATA_FIRST
                }
                resource.fresh = true
            }
            DataFetchStyle.LOCAL_FIRST_UNTIL_STALE -> {
                if (cacheKey == null || sharedPreferences == null) {
                    throw IllegalArgumentException("Cache key and shared preferences required for caching capabilities")
                }
                if (RepositoryUtil.isCacheStale(
                        sharedPreferences,
                        cacheKey,
                        cacheDescriptor,
                        cacheLengthSeconds
                    )
                ) {
                    log("Cache is stale")
                    resource.data = refreshDataFromNetwork(
                        resource,
                        DataFetchStyle.LOCAL_FIRST_UNTIL_STALE
                    )
                    if (resource.data == null) {
                        log("Unsuccessfully stored fresh data from network, getting stale data from local")
                        resource.data = getDataFromLocal()
                        resource.fresh = false
                        resource.dataFetchStyleResult =
                            Result.LOCAL_DATA_NETWORK_FAIL
                    } else {
                        log("Successfully stored fresh data from network")
                        resource.fresh = true
                        resource.dataFetchStyleResult =
                            Result.NETWORK_DATA_LOCAL_STALE
                        RepositoryUtil.resetCache(sharedPreferences, cacheKey, cacheDescriptor)
                    }
                } else {
                    log("Cache isn't stale")
                    resource.data = getDataFromLocal()
                    resource.fresh = true
                    resource.dataFetchStyleResult = Result.LOCAL_DATA_FRESH
                }
            }
        }
        resource.data?.let {
            operateOnDataPostFetch(it)
        }
        return resource
    }

    /**
     * Obtenha dados da rede, tente convertê-los para armazenamento e, em seguida, armazene-os localmente
     * @param resource o recurso para manipular dadas certas circunstâncias
     * @param dataFetchStyle o estilo de busca de dados ao qual este recurso corresponde
     * @return T? - Dados recém-armazenados
     */
    private suspend fun refreshDataFromNetwork(
        resource: Resource<T>,
        dataFetchStyle: DataFetchStyle
    ): T? {
        // Alguns estilos dependem de dados sendo armazenados localmente, emite um erro de registro para informá-los
        val forceStoreLocally = arrayListOf(
            DataFetchStyle.NETWORK_FIRST_LOCAL_FAILOVER,
            DataFetchStyle.LOCAL_FIRST_NETWORK_REFRESH_ALWAYS,
            DataFetchStyle.LOCAL_FIRST_UNTIL_STALE
        ).contains(dataFetchStyle)

        val response: Response<out Any?>
        var convertedToData: T? = null
        var storedFreshData = false
        try {
            response = getDataFromNetwork() //isso pode lançar uma exceção IOException, Timeouts,
            resource.response = response
            log("Got data from network")
            if (resource.response?.body() == null) {
                resource.errorMessage =
                    "Response body was null! Verify correct response object was used for service call"
                Log.e(tag, resource.errorMessage!!)
                //se o corpo for nulo, não adianta tentar convertê-lo ou armazená-lo
                return null
            }

            convertedToData = convertApiResponseToData(response)
            log("Converted data for storage")
            if (forceStoreLocally) {
                storedFreshData = storeFreshDataToLocal(convertedToData)
            }
        } catch (e: Exception) {
            //IOExceptions, conversion exceptions, or local storage exception
            resource.throwable = e
            resource.errorMessage =
                "Unable to refresh data for request type $tag, due to exception $e"
            Log.e(tag, resource.errorMessage!!)
        } finally {
            if (forceStoreLocally && !storedFreshData) {
                Log.e(
                    tag,
                    "$dataFetchStyle requires data to be stored locally for the style to work correctly!"
                )
            }
        }
        return convertedToData
    }

    private fun log(message: String) {
        Log.i(tag, "$tag - $message")
    }
}