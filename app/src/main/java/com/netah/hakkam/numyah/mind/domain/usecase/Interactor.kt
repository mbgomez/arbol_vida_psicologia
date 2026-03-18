package com.netah.hakkam.numyah.mind.domain.usecase


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

interface BaseInteractor<in PARAMS> {
    fun buildUseCase(params: PARAMS): Any

    fun run(params: PARAMS): Any {
        return this.buildUseCase(params)
    }
}

interface BaseInteractorNoParams {
    fun buildUseCase(): Any

    fun run(): Any {
        return this.buildUseCase()
    }
}

abstract class FlowInteractor<in PARAMS, RESULT> : BaseInteractor<PARAMS> {
    abstract override fun buildUseCase(params: PARAMS): Flow<RESULT>

    override fun run(params: PARAMS): Flow<RESULT> {
        return this.buildUseCase(params).flowOn(Dispatchers.IO)
    }
}

abstract class FlowInteractorNoParams<RESULT> : BaseInteractorNoParams {
    abstract override fun buildUseCase(): Flow<RESULT>

    override fun run(): Flow<RESULT> {
        return this.buildUseCase().flowOn(Dispatchers.IO)
    }
}
