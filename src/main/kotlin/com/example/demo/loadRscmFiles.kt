package com.example.demo

import java.io.File
fun loadRscmFiles(directoryPath: String): Map<String, Map<Int, String>> {
    val directory = File(directoryPath)
    val rscmFiles = directory.listFiles { _, name -> name.endsWith(".rscm") } ?: return emptyMap()

    // Map of Maps: Each .rscm file will have its own map
    val mapOfMaps = mutableMapOf<String, Map<Int, String>>()

    rscmFiles.forEach { file ->
        // Use the file name (without extension) as the key in the outer map
        val fileNameWithoutExtension = file.nameWithoutExtension

        val map = mutableMapOf<Int, String>()

        file.readLines().forEach { line ->
            if (line.isBlank()) return@forEach
            val (value, key) = line.split(":").map { it.trim() }
            map[key.toInt()] = "$fileNameWithoutExtension.$value"
        }

        mapOfMaps[fileNameWithoutExtension] = map
    }

    return mapOfMaps
}