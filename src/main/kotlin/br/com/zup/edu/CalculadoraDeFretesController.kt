package br.com.zup.edu

import com.google.protobuf.Any
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import javax.inject.Inject

@Controller
class CalculadoraDeFretesController(
    @Inject val grpcClient: FretesServiceGrpc.FretesServiceBlockingStub
) {

    @Get("/api/fretes")
    fun calcula(@QueryValue cep: String): FreteResponse {

        val request = CalculaFreteRequest
            .newBuilder()
            .setCep(cep)
            .build()

        try {
            val response = grpcClient.calculaFrete(request)
            return FreteResponse(response.cep, response.valor)
        } catch (e: StatusRuntimeException) {
            throw ErrorHandler.tratarErro(e)
        }


    }

}

data class FreteResponse(val cep: String, val valor: Double)