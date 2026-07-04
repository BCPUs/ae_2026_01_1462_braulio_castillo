package com.pucetec.exam2.parking.exceptions

import jakarta.persistence.*

@Entity
@Table(name = "parking_spaces")
class ParkingSpace(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val code: String,

    @Column(nullable = false)
    var isOccupied: Boolean = false
)
