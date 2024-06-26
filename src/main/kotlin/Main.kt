package org.example

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
data class Supplier(val info: String, val type: Int, val reservedDays: List<Int>)
data class Assignment(val dayOfTheMonth: Int, val contractSupplier: String, val stockSupplier: String)

fun main() {
    val serverSocket = ServerSocket(9999)
    println("Sunucu başlatıldı ve 9999 portundan dinliyor")

    while (true) {
        val clientSocket = serverSocket.accept()
        println("Yeni bağlantı: ${clientSocket.inetAddress.hostAddress}")
        handleClient(clientSocket)
    }
}

fun handleClient(socket: Socket) {
    val input = BufferedReader(InputStreamReader(socket.getInputStream()))
    val output = PrintWriter(socket.getOutputStream(), true)
    val request = input.readLine()
    val suppliers = parseRequest(request)
    val assignments = makeAssignments(suppliers)
    val response = Gson().toJson(assignments)
    output.println(response)
    socket.close()
}

fun parseRequest(request: String): List<Supplier> {
    return Gson().fromJson(request, Array<Supplier>::class.java).toList()
}

fun makeAssignments(suppliers: List<Supplier>): List<Assignment> {
    val assignments = mutableListOf<Assignment>()
    for (day in 1..30) {
        val contractSupplier = suppliers.find { it.type == 1 || it.type == 3 }
        val stockSupplier = suppliers.find { it.type == 2 || it.type == 3 }
        if (contractSupplier != null && stockSupplier != null) {
            assignments.add(Assignment(day, contractSupplier.info, stockSupplier.info))
        }
    }
    return assignments
}