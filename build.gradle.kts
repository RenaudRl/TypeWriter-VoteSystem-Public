plugins {
    kotlin("jvm") version "2.3.20"
    id("com.typewritermc.module-plugin") version "2.1.0"
}

repositories {
    maven("https://jitpack.io/")
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://maven.typewritermc.com/beta/")
    maven("https://maven.typewritermc.com/external")
    mavenLocal()
}

group = "btc.renaud"
version = "0.0.6"

typewriter {
    namespace = "btcrenaud"
    extension {
        name = "VoteSystem"
        shortDescription = "Create a Vote System in TypeWriter"
        description = "A comprehensive TypeWriter extension providing advanced gameplay features for Minecraft servers on Paper 1.21+. Fully compatible with the official TypeWriter engine and PlaceholderAPI."
        engineVersion = "0.9.0-beta-175"
        channel = com.typewritermc.moduleplugin.ReleaseChannel.BETA
        paper()
    }
}

    

kotlin {
    jvmToolchain(25)
    
}
