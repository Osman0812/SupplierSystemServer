package org.example

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class Supplier(val info: String, val type: String, val reservedDays: List<Int>)
data class Assignment(val dayOfTheMonth: Int, val contractSupplier: String, val stockSupplier: String)

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            gson()
        }
        routing {
            get("/") {
                call.respondText("hi")
            }
            post("/suppliers") {
                val suppliers = call.receive<List<Supplier>>()
                val assignments = makeAssignments(suppliers)
                call.respond(assignments)
                println(assignments.toString())
            }
        }
    }.start(wait = true)
}

fun makeAssignments(suppliers: List<Supplier>): List<Assignment> {
    val assignments = mutableListOf<Assignment>()
    val usedSuppliersToday = mutableListOf<Supplier>()
    val usedSuppliersYesterday = mutableListOf<Supplier>()
    println(suppliers.toString())

    for (day in 1..30) {
        val availableContractSuppliers = suppliers.filter {
            (it.type == "Contract" || it.type == "Both") &&
                    it !in usedSuppliersToday &&
                    it !in usedSuppliersYesterday &&
                    day !in it.reservedDays
        }

        val availableStockSuppliers = suppliers.filter {
            (it.type == "Stock" || it.type == "Both") &&
                    it !in usedSuppliersToday &&
                    it !in usedSuppliersYesterday &&
                    day !in it.reservedDays
        }

        if (availableContractSuppliers.isNotEmpty()) {
            val contractSupplier = availableContractSuppliers.random()
            if (availableStockSuppliers.isNotEmpty()) {
                val stockSupplier = availableStockSuppliers.random()
                assignments.add(Assignment(day, contractSupplier.info, stockSupplier.info))
                usedSuppliersToday.add(contractSupplier)
                usedSuppliersToday.add(stockSupplier)
            } else {
                val possibleStockSuppliers = suppliers.filter {
                    (it.type == "Stock" || it.type == "Both") &&
                            it !in usedSuppliersToday &&
                            it != contractSupplier &&
                            day !in it.reservedDays
                }

                if (possibleStockSuppliers.isNotEmpty()) {
                    val stockSupplier = possibleStockSuppliers.random()
                    assignments.add(Assignment(day, contractSupplier.info, stockSupplier.info))
                    usedSuppliersToday.add(contractSupplier)
                    usedSuppliersToday.add(stockSupplier)
                } else {
                    assignments.add(Assignment(day, contractSupplier.info, contractSupplier.info))
                    usedSuppliersToday.add(contractSupplier)
                }
            }
        } else {
            if (availableStockSuppliers.isNotEmpty()) {
                val stockSupplier = availableStockSuppliers.random()
                val possibleContractSuppliers = suppliers.filter {
                    (it.type == "Contract" || it.type == "Both") &&
                            it !in usedSuppliersToday &&
                            it != stockSupplier &&
                            day !in it.reservedDays
                }

                if (possibleContractSuppliers.isNotEmpty()) {
                    val contractSupplier = possibleContractSuppliers.random()
                    assignments.add(Assignment(day, contractSupplier.info, stockSupplier.info))
                    usedSuppliersToday.add(contractSupplier)
                    usedSuppliersToday.add(stockSupplier)
                } else {
                    assignments.add(Assignment(day, stockSupplier.info, stockSupplier.info))
                    usedSuppliersToday.add(stockSupplier)
                }
            }
        }
        usedSuppliersYesterday.clear()
        usedSuppliersYesterday.addAll(usedSuppliersToday)
        usedSuppliersToday.clear()
    }
    return assignments
}

