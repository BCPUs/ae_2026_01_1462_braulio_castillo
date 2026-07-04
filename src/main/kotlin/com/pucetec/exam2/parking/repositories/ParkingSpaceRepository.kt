package com.pucetec.exam2.parking.repositories

import com.pucetec.exam2.parking.exceptions.ParkingSpace
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ParkingSpaceRepository : JpaRepository<ParkingSpace, Long> {
    fun findByIsOccupiedFalse(): List<ParkingSpace>
    fun countByIsOccupiedTrue(): Long
}