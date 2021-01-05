package br.com.eduardotanaka.warren.ui.base

import androidx.annotation.StringRes
import br.com.eduardotanaka.warren.data.repository.base.Resource

/**
 * Classe Wrapper para emitir facilmente informações de estado em torno de um recurso
 * por exemplo. LiveData <StatefulResource <Artist>>
 */
class StatefulResource<T> {
    /**
     * Vários estados em que o recurso pode estar
     */
    enum class State {
        LOADING,
        SUCCESS, //não garante hasData!
        ERROR,
        ERROR_API,
        ERROR_NETWORK
    }

    /**
     * Estado atual
     */
    var state = State.SUCCESS
        private set

    fun setState(state: State): StatefulResource<T> {
        this.state = state
        return this
    }

    fun isSuccessful() = state == State.SUCCESS

    fun isLoading() = state == State.LOADING

    /**
     * O recurso correspondente
     */
    var resource: Resource<T>? = null

    fun getData(): T? = resource?.data

    fun hasData(): Boolean = resource?.hasData() ?: false

    /**
     * Recurso de mensagem personalizada
     */
    @StringRes
    var message: Int? = null
        private set

    fun setMessage(@StringRes message: Int): StatefulResource<T> {
        this.message = message
        return this
    }

    companion object {
        fun <S : Any?> with(state: State, resource: Resource<S>? = null): StatefulResource<S> {
            return StatefulResource<S>().apply {
                setState(state)
                this.resource = resource
            }
        }

        fun <S : Any?> success(resource: Resource<S>? = null): StatefulResource<S> {
            return StatefulResource<S>().apply {
                setState(State.SUCCESS)
                this.resource = resource
            }
        }

        fun <S : Any?> loading(): StatefulResource<S> {
            return StatefulResource<S>().apply {
                setState(State.LOADING)
            }
        }
    }
}
