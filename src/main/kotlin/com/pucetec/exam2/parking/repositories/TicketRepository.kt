package com.pucetec.exam2.parking.repositories

import com.pucetec.exam2.parking.entities.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : JpaRepository<Ticket, Long> {
    // Busca si ya existe un ticket abierto (exitTime es nulo) para esa placa
    fun existsByLicensePlateAndExitTimeIsNull(licensePlate: String): Boolean
}