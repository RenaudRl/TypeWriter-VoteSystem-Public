repositories {
     mavenCentral()
}
dependencies {
    implementation(kotlin("reflect"))
    // Coroutine support for cooldown handling in the poll manager
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

plugins {
    kotlin("jvm") version "2.2.10"
    id("com.typewritermc.module-plugin")
}

group = "btc.renaud"
version = "0.0.1"

// Ensure Gradle always writes a standard zip structure so the resulting
// extension jar can be parsed without "invalid LOC header" errors.
tasks.withType<Jar>().configureEach {
    isZip64 = true
}

typewriter {
    namespace = "renaud"

    extension {
        name = "VoteSystem"
        shortDescription = "Create a Vote System in TypeWriter"
        description = """
            |A Vote System for Typewriter that allows players to vote (trigger can be send at the end of the cooldown)
            |Multiples menus, organized by status and tracking progress with quest categories.
            """.trimMargin()
        engineVersion = file("../../version.txt").readText().trim()
        channel = com.typewritermc.moduleplugin.ReleaseChannel.BETA

        dependencies {
            paper()
        }
    }
}

kotlin {
    jvmToolchain(21)
}

