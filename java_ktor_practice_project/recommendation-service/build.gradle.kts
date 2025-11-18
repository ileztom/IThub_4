plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "com.example"
version = "0.0.1"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.example.recommendation.ApplicationKt")
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-jackson:2.3.7")

    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

    implementation("org.postgresql:postgresql:42.6.0")

    implementation("org.apache.kafka:kafka-clients:3.6.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")

    implementation("ch.qos.logback:logback-classic:1.4.14")
}

kotlin {
    jvmToolchain(17)
}

/**
 * Выполняем FAT-JAR вручную (надёжнее ShadowJar)
 */
tasks.jar {
    archiveBaseName.set("recommendation-service")
    archiveVersion.set("0.0.1")
    archiveClassifier.set("") // без "-plain"
}

