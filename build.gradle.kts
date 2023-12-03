import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.9.20"

    id("org.jlleitschuh.gradle.ktlint") version "11.5.0"
    id("signing")
    id("maven-publish")
    id("io.codearte.nexus-staging") version "0.30.0"
    id("io.kotest.multiplatform") version "5.8.0"
}

group = "io.github.yuitosato"
version = "1.5.0"

repositories {
    mavenCentral()
}

val kotestVersion = "5.8.0"

val jvmVersion = 11

kotlin {
    jvmToolchain(jvmVersion)

    jvm() // JVMターゲット
    js(IR) { // JavaScriptターゲット（IRベース）
        browser()
        nodejs()
    }
//    androidTarget()
    iosArm64()
    iosSimulatorArm64()
//    iosX64("ios") // iOSターゲット（ここではX64のみを例としています）
    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {}
        val commonTest by getting {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:$kotestVersion")
                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {}
        val jvmTest by getting {
            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
            }
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:$kotestVersion")
            }
        }

        val jsMain by getting {}
        val jsTest by getting {}

        val iosMain by getting {}
        val iosTest by getting {}
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = jvmVersion.toString()
}

java {
//    withJavadocJar()
//    withSourcesJar()
}

nexusStaging {
    serverUrl = "https://s01.oss.sonatype.org/service/local/"
    packageGroup = "io.github.yuitosato"

    val sonatypeUsername: String? by project
    val sonatypePassword: String? by project
    username = sonatypeUsername
    password = sonatypePassword
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "kotlin-tree"
//            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("kotlin-tree")
                description.set("Kotlin Declarative APIs for Multi-way Tree Data.")
                url.set("https://github.com/YuitoSato/kotlin-tree")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("YuitoSato")
                        name.set("Yuito Sato")
                        email.set("yuitosato.w@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:YuitoSato/kotlin-tree.git")
                    developerConnection.set("git@github.com:YuitoSato/kotlin-tree.git")
                    url.set("https://github.com/YuitoSato/kotlin-tree")
                }
            }
        }
    }
    repositories {
        maven {
            credentials {
                val sonatypeUsername: String? by project
                val sonatypePassword: String? by project
                username = sonatypeUsername
                password = sonatypePassword
            }

            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}
//
// tasks.withType<KotlinCompile> {
//    kotlinOptions.jvmTarget = jvmVersion.toString()
// }

// tasks.withType<Test>().configureEach {
//    useJUnitPlatform()
// }

// tasks.withType<Javadoc>().configureEach {
//    if (JavaVersion.current().isJava9Compatible) {
//        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
//    }
// }
//
// tasks.register<Jar>("javadocJar") {
//    archiveClassifier.set("javadoc")
//    from(tasks.withType<Javadoc>())
// }

signing {
    sign(publishing.publications["mavenJava"])
}

// tasks.register<Jar>("sourcesJar") {
//    archiveClassifier.set("sources")
//    from(sourceSets["main"].allSource)
// }
//
// tasks.register<Jar>("javadocJar") {
//    archiveClassifier.set("javadoc")
//    from(tasks.javadoc())
// }

// tasks.javadoc {
//    if (JavaVersion.current().isJava9Compatible) {
//        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
//    }
// }
//
// tasks.register("closeAndReleaseRepositoryIfProductionRelease") {
//    if (!version.toString().endsWith("SNAPSHOT")) {
//        dependsOn("closeAndReleaseRepository")
//    }
// }
