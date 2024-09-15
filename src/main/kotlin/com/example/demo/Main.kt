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
        exampleFunction(var_value, 124) // this does not get replaced due to variable changing later

        for(i in 0.. 300) {
            var_value = i
            exampleFunction(var_value, 124)
        }
        var beans = 123
        if(true) {
            exampleFunction(beans, 124) // this gets replaced
        }
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