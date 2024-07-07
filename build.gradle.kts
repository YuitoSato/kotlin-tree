import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.9.22"

    id("org.jlleitschuh.gradle.ktlint") version "11.5.0"
    id("signing")
    id("maven-publish")
    id("io.codearte.nexus-staging") version "0.30.0"
    id("io.kotest.multiplatform") version "5.8.0"
    id("org.jetbrains.dokka") version "1.9.10"
    id("com.android.library") version "8.2.0"
    `maven-publish`
}

group = "io.github.yuitosato"
version = "1.5.0_SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

val kotestVersion = "5.9.0"

val jvmVersion = 11

kotlin {
    applyDefaultHierarchyTemplate()
    jvm {
        withSourcesJar()
    }
    jvmToolchain(jvmVersion)

    js(IR) {
        browser()
        nodejs()
    }

    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    /* https://kotlinlang.org/docs/native-target-support.html#tier-1 */

    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()

    /* https://kotlinlang.org/docs/native-target-support.html#tier-2 */

    linuxX64()
    linuxArm64()

    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()

    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()

    iosArm64()

    sourceSets {
        val commonMain by getting {
            tasks.withType<KotlinCompile> {
                kotlinOptions.jvmTarget = jvmVersion.toString()
            }

            tasks.withType<Javadoc>().configureEach {
                if (JavaVersion.current().isJava9Compatible) {
                    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
                }
            }

            tasks.register("closeAndReleaseRepositoryIfProductionRelease") {
                if (!version.toString().endsWith("SNAPSHOT")) {
                    dependsOn("closeAndReleaseRepository")
                }
            }

            tasks.register<Jar>("javadocJar") {
                archiveClassifier.set("javadoc")
                from(tasks.withType<Javadoc>())
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-framework-engine:$kotestVersion")
                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
            }
        }

        val jvmTest by getting {
            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
            }
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:$kotestVersion")
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = jvmVersion.toString()
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
    plugins.withType<MavenPublishPlugin> {
        apply(plugin = "org.gradle.signing")

        plugins.withType<KotlinMultiplatformPluginWrapper> {
            apply(plugin = "org.jetbrains.dokka")
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "kotlin-tree"
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

signing {
    sign(publishing.publications["mavenJava"])
}

android {
    namespace = "io.github.yuitosato.kotlintree"
    compileSdk = 34
    defaultConfig {
        minSdk = 28
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
