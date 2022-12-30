import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application

    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

group = "yuitosato"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotestVersion = "5.5.4"

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}
