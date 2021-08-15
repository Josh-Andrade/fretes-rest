package br.com.zup.edu

import com.google.protobuf.Any
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import java.lang.StringBuilder

enum class ErrorHandler(private val grpcStatus: Status, private val status: HttpStatus) {

    INVALID_ARGUMENT(Status.INVALID_ARGUMENT, HttpStatus.BAD_REQUEST),
    INTERNAL(Status.INTERNAL, HttpStatus.INTERNAL_SERVER_ERROR),
    PERMISSION_DENIED(Status.PERMISSION_DENIED, HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(Status.UNAUTHENTICATED, HttpStatus.UNAUTHORIZED),
    OUT_OF_RANGE(Status.OUT_OF_RANGE, HttpStatus.BAD_REQUEST),
    NOT_FOUND(Status.NOT_FOUND, HttpStatus.NOT_FOUND),
    ABORTED(Status.ABORTED, HttpStatus.CONFLICT),
    ALREADY_EXISTS(Status.ALREADY_EXISTS, HttpStatus.CONFLICT),
    UNKNOWN(Status.UNKNOWN, HttpStatus.INTERNAL_SERVER_ERROR),
    UNAVAILABLE(Status.UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE),
    FAILED_PRECONDITION(Status.FAILED_PRECONDITION, HttpStatus.BAD_REQUEST),
    RESOURCE_EXHAUSTED(Status.RESOURCE_EXHAUSTED, HttpStatus.TOO_MANY_REQUESTS),
    DATA_LOSS(Status.DATA_LOSS, HttpStatus.INTERNAL_SERVER_ERROR),
    DEADLINE_EXCEEDED(Status.DEADLINE_EXCEEDED, HttpStatus.GATEWAY_TIMEOUT);

    companion object {
        private fun getRestStatus(code: Status.Code): HttpStatus {
            val filterError = values()
                .filter { errorHandler -> errorHandler.grpcStatus.code == code }
            return filterError[0].status
        }

        private fun getErrorDetailsMessage(errorDetails: List<ErrorDetails>?): String{
            val errorMessage = StringBuilder("")
            errorDetails
                ?.forEach { it -> errorMessage.append("${it.code}: ${it.message} ") }
            return errorMessage.toString()
        }

        fun tratarErro(e: StatusRuntimeException): Throwable {
            val detailsList: List<ErrorDetails> ? = StatusProto.fromThrowable(e)?.
                detailsList?.
                map { it.unpack(ErrorDetails::class.java) }?.
                toList()

            val errorDetailsMessage = getErrorDetailsMessage(detailsList)
            if(errorDetailsMessage.isNotBlank()){
                throw HttpStatusException(getRestStatus(e.status.code), errorDetailsMessage)
            }
            throw HttpStatusException(getRestStatus(e.status.code), e.status.description)
        }
    }


}