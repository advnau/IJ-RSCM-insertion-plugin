package com.example.demo

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        exampleFunction(123, 124)

        exampleFunction(123, 124)

    }


    fun exampleFunction(param1: Int, param2: Int) {
        println(param1)
        println(param2)
    }

    fun exampleFunction(param1: String, param2: Int) {
        println(param1)
        println(param2)
    }

    fun exampleFunction(param1: Int, param2: String) {
        println(param1)
        println(param2)
    }
}