package com.pucetec.exam2.parking.mappers

import com.pucetec.exam2.parking.dto.ParkingSpaceResponse
import com.pucetec.exam2.parking.exceptions.ParkingSpace

fun ParkingSpace.toResponse() = ParkingSpaceResponse(
    id = this.id,
    code = this.code,
    isOccupied = this.isOccupied
)
