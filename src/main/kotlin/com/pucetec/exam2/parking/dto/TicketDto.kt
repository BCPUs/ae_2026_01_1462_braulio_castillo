package com.pucetec.exam2.parking.dto
import java.time.LocalDateTime
data class TicketEntryRequest(
    val licensePlate: String
)

data class TicketResponse(
    val id: Long,
    val licensePlate: String,
    val entryTime: LocalDateTime,
    val exitTime: LocalDateTime?,
    val spaceCode: String
)