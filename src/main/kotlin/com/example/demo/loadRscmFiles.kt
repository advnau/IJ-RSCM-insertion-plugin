package com.example.demo

import java.nio.file.Path
import java.nio.file.Paths

fun loadRscmFiles(directoryPath: String): Map<String, Map<Int, String>> {
    val normalizedPath: Path = Paths.get(directoryPath).toAbsolutePath().normalize()
    val directory = normalizedPath.toFile()
    if (!directory.isDirectory) {
        return emptyMap()
    }

    val rscmFiles = directory.listFiles { _, name -> name.endsWith(".rscm") } ?: return emptyMap()

    // Map of Maps: Each .rscm file will have its own map
    val mapOfMaps = mutableMapOf<String, Map<Int, String>>()

    rscmFiles.forEach { file ->
        // Use the file name (without extension) as the key in the outer map
        val fileNameWithoutExtension = file.nameWithoutExtension

        val map = mutableMapOf<Int, String>()

        file.readLines().forEach { line ->
            if (line.isBlank() || !line.contains(":")) return@forEach
            val parts = line.split(":")
            if (parts.size < 2) return@forEach
            val value = parts[0].trim()
            val key = parts[1].trim()
            key.toIntOrNull()?.let {
                map[it] = "$fileNameWithoutExtension.$value"
            }
        }

        mapOfMaps[fileNameWithoutExtension] = map
    }

    return mapOfMaps
}
