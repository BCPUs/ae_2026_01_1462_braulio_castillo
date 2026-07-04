package com.pucetec.exam2.parking.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tickets")
class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val licensePlate: String,

    @Column(nullable = false)
    val entryTime: LocalDateTime = LocalDateTime.now(),

    @Column
    var exitTime: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    val space: ParkingSpace
)