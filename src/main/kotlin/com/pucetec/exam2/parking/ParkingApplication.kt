package com.pucetec.exam2.parking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ParkingApplication

fun main(args: Array<String>) {
	runApplication<ParkingApplication>(*args)
}
