package com.pucetec.exam2.parking.services

import com.pucetec.exam2.parking.dto.ParkingSpaceResponse
import com.pucetec.exam2.parking.dto.TicketEntryRequest
import com.pucetec.exam2.parking.dto.TicketResponse
import com.pucetec.exam2.parking.entities.Ticket
import com.pucetec.exam2.parking.exceptions.InvalidTicketStateException
import com.pucetec.exam2.parking.exceptions.ParkingFullException
import com.pucetec.exam2.parking.exceptions.ResourceNotFoundException
import com.pucetec.exam2.parking.mappers.toResponse
import com.pucetec.exam2.parking.repositories.ParkingSpaceRepository
import com.pucetec.exam2.parking.repositories.TicketRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ParkingService(
    private val spaceRepository: ParkingSpaceRepository,
    private val ticketRepository: TicketRepository
) {
    private val log = LoggerFactory.getLogger(ParkingService::class.java)
    private val capacidad = 20

    @Transactional(readOnly = true)
    fun getAvailableSpaces(): List<ParkingSpaceResponse> {
        log.info("Consultando espacios de estacionamiento disponibles")
        return spaceRepository.findByIsOccupiedFalse().map { it.toResponse() }
    }

    @Transactional
    fun registerEntry(request: TicketEntryRequest): TicketResponse {
        log.info("Registrando entrada para vehiculo: ${request.licensePlate}")

        val occupiedCount = spaceRepository.countByIsOccupiedTrue()
        if (occupiedCount >= capacidad) {
            log.warn("Intento de entrada fallido: Estacionamiento lleno")
            throw ParkingFullException("El estacionamiento ha alcanzado su capacidad maxima de $capacidad espacios.")
        }

        val availableSpaces = spaceRepository.findByIsOccupiedFalse()
        if (availableSpaces.isEmpty()) {
            throw ParkingFullException("No hay espacios disponibles (Lleno lógico).")
        }

        val spaceToAssign = availableSpaces.first()
        spaceToAssign.isOccupied = true
        spaceRepository.save(spaceToAssign)

        val ticket = Ticket(
            licensePlate = request.licensePlate,
            space = spaceToAssign
        )
        val savedTicket = ticketRepository.save(ticket)

        log.info("Entrada registrada exitosamente. Ticket ID: ${savedTicket.id}")
        return savedTicket.toResponse()
    }

    @Transactional
    fun registerExit(ticketId: Long): TicketResponse {
        log.info("Registrando salida para el ticket ID: $ticketId")

        val ticket = ticketRepository.findById(ticketId).orElseThrow {
            ResourceNotFoundException("No se encontro el ticket con ID: $ticketId")
        }

        if (ticket.exitTime != null) {
            log.warn("Intento de salida en un ticket ya cerrado. Ticket ID: $ticketId")
            throw InvalidTicketStateException("El vehiculo ya registro su salida previamente y el ticket esta cerrado.")
        }

        ticket.exitTime = LocalDateTime.now()

        val space = ticket.space
        space.isOccupied = false
        spaceRepository.save(space)

        val updatedTicket = ticketRepository.save(ticket)

        log.info("Salida registrada exitosamente. Espacio ${space.code} liberado.")
        return updatedTicket.toResponse()
    }
}

