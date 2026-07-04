package com.pucetec.exam2.parking.services

import com.pucetec.exam2.parking.exceptions.ParkingSpace
import com.pucetec.exam2.parking.entities.Ticket
import com.pucetec.exam2.parking.dto.TicketEntryRequest
import com.pucetec.exam2.parking.exceptions.InvalidTicketStateException
import com.pucetec.exam2.parking.exceptions.ParkingFullException
import com.pucetec.exam2.parking.exceptions.ResourceNotFoundException
import com.pucetec.exam2.parking.repositories.ParkingSpaceRepository
import com.pucetec.exam2.parking.repositories.TicketRepository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.never
import org.mockito.ArgumentMatchers.any

import java.time.LocalDateTime
import java.util.Optional

class ParkingServiceTest {

    private lateinit var spaceRepository: ParkingSpaceRepository
    private lateinit var ticketRepository: TicketRepository
    private lateinit var parkingService: ParkingService

    @BeforeEach
    fun setUp() {
        spaceRepository = mock(ParkingSpaceRepository::class.java)
        ticketRepository = mock(TicketRepository::class.java)
        parkingService = ParkingService(spaceRepository, ticketRepository)
    }

    @Test
    fun `getAvailableSpaces should return only unoccupied spaces`() {
        val space = ParkingSpace(id = 1L, code = "A1", isOccupied = false)
        `when`(spaceRepository.findByIsOccupiedFalse()).thenReturn(listOf(space))

        val result = parkingService.getAvailableSpaces()

        assertEquals(1, result.size)
        assertEquals("A1", result[0].code)
        verify(spaceRepository).findByIsOccupiedFalse()
    }

    @Test
    fun `registerEntry should assign space and create ticket`() {
        val request = TicketEntryRequest("ABC-1234")
        val space = ParkingSpace(id = 1L, code = "A1", isOccupied = false)
        val ticket = Ticket(id = 1L, licensePlate = "ABC-1234", space = space)

        `when`(spaceRepository.countByIsOccupiedTrue()).thenReturn(10L)
        `when`(spaceRepository.findByIsOccupiedFalse()).thenReturn(listOf(space))
        `when`(spaceRepository.save(any())).thenReturn(space)
        `when`(ticketRepository.save(any())).thenReturn(ticket)

        val result = parkingService.registerEntry(request)

        assertEquals("ABC-1234", result.licensePlate)
        assertEquals("A1", result.spaceCode)
        assertTrue(space.isOccupied)
        verify(spaceRepository).save(space)
        verify(ticketRepository).save(any())
    }

    @Test
    fun `registerEntry should throw ParkingFullException when capacity is reached`() {
        val request = TicketEntryRequest("ABC-1234")
        `when`(spaceRepository.countByIsOccupiedTrue()).thenReturn(20L)

        assertThrows<ParkingFullException> {
            parkingService.registerEntry(request)
        }

        verify(spaceRepository, never()).findByIsOccupiedFalse()
        verify(ticketRepository, never()).save(any())
    }

    @Test
    fun `registerEntry should throw ParkingFullException when no spaces available logically`() {
        val request = TicketEntryRequest("ABC-1234")
        `when`(spaceRepository.countByIsOccupiedTrue()).thenReturn(19L)
        `when`(spaceRepository.findByIsOccupiedFalse()).thenReturn(emptyList())

        assertThrows<ParkingFullException> {
            parkingService.registerEntry(request)
        }
    }

    @Test
    fun `registerExit should close ticket and free space`() {
        val space = ParkingSpace(id = 1L, code = "A1", isOccupied = true)
        val ticket = Ticket(id = 1L, licensePlate = "ABC-1234", space = space, exitTime = null)

        `when`(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket))
        `when`(spaceRepository.save(any())).thenReturn(space)
        `when`(ticketRepository.save(any())).thenReturn(ticket)

        val result = parkingService.registerExit(1L)

        assertNotNull(result.exitTime)
        assertFalse(space.isOccupied)
        verify(spaceRepository).save(space)
        verify(ticketRepository).save(ticket)
    }

    @Test
    fun `registerExit should throw ResourceNotFoundException when ticket does not exist`() {
        `when`(ticketRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            parkingService.registerExit(99L)
        }
        verify(spaceRepository, never()).save(any())
    }

    @Test
    fun `registerExit should throw InvalidTicketStateException when ticket already closed`() {
        val space = ParkingSpace(id = 1L, code = "A1", isOccupied = false)
        val closedTicket = Ticket(
            id = 1L,
            licensePlate = "ABC-1234",
            space = space,
            exitTime = LocalDateTime.now()
        )

        `when`(ticketRepository.findById(1L)).thenReturn(Optional.of(closedTicket))

        assertThrows<InvalidTicketStateException> {
            parkingService.registerExit(1L)
        }
        verify(spaceRepository, never()).save(any())
    }
}