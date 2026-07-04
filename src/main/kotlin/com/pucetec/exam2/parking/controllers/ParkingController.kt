package com.pucetec.exam2.parking.controllers

import com.pucetec.exam2.parking.dto.ParkingSpaceResponse
import com.pucetec.exam2.parking.dto.TicketEntryRequest
import com.pucetec.exam2.parking.dto.TicketResponse
import com.pucetec.exam2.parking.services.ParkingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ParkingController(
    private val parkingService: ParkingService
) {

    @GetMapping("/spaces")
    fun getAvailableSpaces(): ResponseEntity<List<ParkingSpaceResponse>> {
        return ResponseEntity.ok(parkingService.getAvailableSpaces())
    }

    @PostMapping("/entry")
    fun registerEntry(@RequestBody request: TicketEntryRequest): ResponseEntity<TicketResponse> {
        val response = parkingService.registerEntry(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/exit/{ticketId}")
    fun registerExit(@PathVariable ticketId: Long): ResponseEntity<TicketResponse> {
        val response = parkingService.registerExit(ticketId)
        return ResponseEntity.ok(response)
    }
}
