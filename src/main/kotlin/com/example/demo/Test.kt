package com.example.demo

object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val providedPath = args.firstOrNull()
        val fallbackPath = System.getenv("RSCM_DIRECTORY")
        val data = loadRscmFiles(providedPath ?: fallbackPath ?: System.getProperty("user.home") + "/rscm")
        println(data)
    }
}
