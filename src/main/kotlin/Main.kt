package org.example

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
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
    val usedSuppliersToday = mutableSetOf<Supplier>()
    val usedSuppliersYesterday = mutableSetOf<Supplier>()

    for (day in 1..10) {
        val availableContractSuppliers = suppliers.filter { it.type == "Contract" || it.type == "Both" }
            .filter { it !in usedSuppliersToday }
        val availableStockSuppliers = suppliers.filter { it.type == "Stock" || it.type == "Both" }
            .filter { it !in usedSuppliersToday }

        if (availableContractSuppliers.isNotEmpty() && availableStockSuppliers.isNotEmpty()) {
            val contractSupplier = availableContractSuppliers.random()
            val stockSupplier = availableStockSuppliers.random()
            assignments.add(Assignment(day, contractSupplier.info, stockSupplier.info))
            usedSuppliersToday.add(contractSupplier)
            usedSuppliersToday.add(stockSupplier)
        } else if (availableContractSuppliers.isNotEmpty() && availableStockSuppliers.isEmpty()) {
            val contractSupplier = availableContractSuppliers.random()
            assignments.add(Assignment(day, contractSupplier.info, contractSupplier.info))
            usedSuppliersToday.add(contractSupplier)
        } else if (availableContractSuppliers.isEmpty() && availableStockSuppliers.isNotEmpty()) {
            val stockSupplier = availableStockSuppliers.random()
            assignments.add(Assignment(day, stockSupplier.info, stockSupplier.info))
            usedSuppliersToday.add(stockSupplier)
        }

        usedSuppliersYesterday.clear()
        usedSuppliersYesterday.addAll(usedSuppliersToday)
        usedSuppliersToday.clear()
    }
    return assignments
}