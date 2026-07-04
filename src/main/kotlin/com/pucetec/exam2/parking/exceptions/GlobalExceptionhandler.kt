package com.pucetec.exam2.parking.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(e: ResourceNotFoundException): ResponseEntity<Map<String, Any>> {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.message ?: "Resource not found")
    }

    @ExceptionHandler(ParkingFullException::class)
    fun handleConflict(e: ParkingFullException): ResponseEntity<Map<String, Any>> {
        return buildErrorResponse(HttpStatus.CONFLICT, e.message ?: "Conflict")
    }

    @ExceptionHandler(InvalidTicketStateException::class)
    fun handleBadRequest(e: InvalidTicketStateException): ResponseEntity<Map<String, Any>> {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.message ?: "Bad request")
    }

    private fun buildErrorResponse(status: HttpStatus, message: String): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(status).body(
            mapOf(
                "timestamp" to LocalDateTime.now(),
                "status" to status.value(),
                "error" to status.reasonPhrase,
                "message" to message
            )
        )
    }
}
