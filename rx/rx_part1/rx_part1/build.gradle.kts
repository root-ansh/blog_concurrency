import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.6.21"
    application
}
group = "work.curioustools"
version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

tasks.test{ useJUnitPlatform() }
tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

application.mainClass.set("MainKt")

dependencies { testImplementation(kotlin("test")) }

