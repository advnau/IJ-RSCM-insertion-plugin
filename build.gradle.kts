plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.intellij") version "1.12.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin standard library
    implementation(kotlin("stdlib"))

    // Kotlin Plugin SDK for working with Kotlin PSI in IntelliJ
//    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.0")  // Use the version of Kotlin you're using

    // This dependency includes Kotlin PSI elements like KtPsiFactory, KtCallExpression, etc.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
//
//    // IntelliJ Platform SDK
//    implementation("org.jetbrains.intellij.sdk:openapi:IC-2021.1")
//    implementation("org.jetbrains.intellij.sdk:idea:IC-2021.1")
//
//    // Kotlin PSI Visitor and other utilities
//    implementation("org.jetbrains.kotlin:kotlin-plugin-idea:1.9.0")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
//    version.set("2022.1.4")
    version.set("2023.3.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.intellij.java", "org.jetbrains.kotlin"/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("221")
//        untilBuild.set("231.*")
        untilBuild.set("234.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
