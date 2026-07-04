package com.pucetec.exam2.parking.services

import com.pucetec.exam2.parking.dto.TicketEntryRequest
import com.pucetec.exam2.parking.exceptions.VehicleAlreadyParkedException
import com.pucetec.exam2.parking.repositories.ParkingSpaceRepository
import com.pucetec.exam2.parking.repositories.TicketRepository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.never
import org.mockito.ArgumentMatchers.any

class AdditionalExceptionTest {

    private lateinit var spaceRepository: ParkingSpaceRepository
    private lateinit var ticketRepository: TicketRepository
    private lateinit var parkingService: ParkingService

    @BeforeEach
    fun setUp() {
        // Inicializamos los mocks necesarios para esta clase de prueba
        spaceRepository = mock(ParkingSpaceRepository::class.java)
        ticketRepository = mock(TicketRepository::class.java)
        parkingService = ParkingService(spaceRepository, ticketRepository)
    }

    @Test
    fun `registerEntry should throw VehicleAlreadyParkedException when vehicle is already parked`() {
        // Arrange
        val request = TicketEntryRequest("ABC-1234")
        `when`(ticketRepository.existsByLicensePlateAndExitTimeIsNull("ABC-1234")).thenReturn(true)

        // Act & Assert
        assertThrows<VehicleAlreadyParkedException> {
            parkingService.registerEntry(request)
        }

        // Verificamos que el flujo se detenga inmediatamente y no guarde nada
        verify(spaceRepository, never()).findByIsOccupiedFalse()
        verify(ticketRepository, never()).save(any())
    }
}