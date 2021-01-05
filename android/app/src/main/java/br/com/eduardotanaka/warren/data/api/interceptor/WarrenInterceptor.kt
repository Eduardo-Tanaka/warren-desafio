package br.com.eduardotanaka.warren.data.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class WarrenInterceptor : Interceptor {

    companion object {
        const val ACCEPT = "Accept"
        const val APPLICATION_JSON = "application/json"
    }

    // intercepta todas as requisicoes para adicionar no header "Accept": "application/json"
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request()
            .url
            .newBuilder()
            .build()
        val request = chain.request()
            .newBuilder()
            .addHeader(
                ACCEPT,
                APPLICATION_JSON
            )
            .url(url)
            .build()

        return chain.proceed(request)
    }
}
