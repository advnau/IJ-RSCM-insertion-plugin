package com.example.demo

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        exampleFunction(123, 124)

        exampleFunction(123, 124)

        val constant_value = 123;
        exampleFunction(constant_value, 124)// this replaces

        var var_value = 123;
        var_value = 124
        exampleFunction(var_value, 124) // this replaces with 123, but it shouldnt

        exampleFunction(CONST_VAL, 124) // this replaces
    }

    const val CONST_VAL = 123

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