package com.pucetec.exam2.parking.dto

data class ParkingSpaceResponse(
    val id: Long,
    val code: String,
    val isOccupied: Boolean
)
