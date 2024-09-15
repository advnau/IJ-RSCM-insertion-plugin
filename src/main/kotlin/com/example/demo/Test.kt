package com.example.demo

object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val data = loadRscmFiles("C:\\Users\\Home\\Downloads\\rscm\\")
        println(data)
    }
}