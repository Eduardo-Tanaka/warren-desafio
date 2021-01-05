package br.com.eduardotanaka.warren.data.model.entity.base

import br.com.eduardotanaka.warren.data.model.api.base.ApiResponseObject

/**
 * Isso vincula um contrato entre o modelo armazenado localmente e uma resposta da API
 *
 * A ideia é que os modelos armazenados localmente NÃO devem ser o mesmo modelo que recebemos da solicitação de API,
 * esta interface define como eles estão relacionados.
 *
 * Recursive Generics
 * @param X O modelo que reflete o ApiResponseObject
 * @param T : ApiResponseObject
 */
interface ReflectsApiResponse<X, T : ApiResponseObject> {
    /**
     * Mapeia do ApiResponse especificado e retorne a sua instância
     * @param apiResponse T
     * @return
     */
    fun reflectFrom(apiResponse: T): X
}