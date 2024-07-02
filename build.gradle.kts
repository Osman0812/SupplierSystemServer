plugins {
    kotlin("jvm") version "2.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("io.ktor:ktor-server-core:2.0.0")
    implementation("io.ktor:ktor-server-netty:2.0.0")
    implementation("io.ktor:ktor-server-host-common:2.0.0")
    implementation("io.ktor:ktor-server-status-pages:2.0.0")
    implementation("ch.qos.logback:logback-classic:1.2.10")
    testImplementation("io.ktor:ktor-server-tests:2.0.0")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.0")
    implementation("io.ktor:ktor-serialization-gson:2.0.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.7.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}