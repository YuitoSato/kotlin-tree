import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"

    id("org.jlleitschuh.gradle.ktlint") version "11.1.0"
    id("signing")
    id("maven-publish")
    id("io.codearte.nexus-staging") version "0.30.0"
}

group = "io.github.yuitosato"
version = "1.4.2-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotestVersion = "5.5.5"

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks.test {
    useJUnitPlatform()
}

val jvmVersion = 17

kotlin {
    jvmToolchain {
        (this).languageVersion.set(JavaLanguageVersion.of(jvmVersion))
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = jvmVersion.toString()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

java {
    withJavadocJar()
    withSourcesJar()
}

nexusStaging {
    serverUrl = "https://s01.oss.sonatype.org/service/local/"
    packageGroup = "io.github.yuitosato"
    username = System.getenv("SONATYPE_USERNAME")
    password = System.getenv("SONATYPE_PASSWORD")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "kotlin-tree"
            from(components["java"])
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
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }

            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

signing {
    val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
    val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
    val signingKeyFile = file("${System.getenv("GITHUB_WORKSPACE")}/secring.gpg")
    val signingKey = signingKeyFile.readText()

    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks.register("publishProject") {
    if (!version.toString().endsWith("SNAPSHOT")) {
        dependsOn("publish", "closeAndReleaseRepository")
    } else {
        dependsOn("publish")
    }
}
