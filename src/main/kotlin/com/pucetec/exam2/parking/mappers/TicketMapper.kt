package com.pucetec.exam2.parking.mappers

import com.pucetec.exam2.parking.dto.TicketResponse
import com.pucetec.exam2.parking.entities.Ticket

fun Ticket.toResponse() = TicketResponse(
    id = this.id,
    licensePlate = this.licensePlate,
    entryTime = this.entryTime,
    exitTime = this.exitTime,
    spaceCode = this.space.code
)
