plugins {
    kotlin("jvm") version "1.9.0"
}

group = "work.curioustools"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // architecture: json (yes, JDK does not have it! it's provided by android)
    implementation("org.json:json:20231013")


    // architecture: coroutines, coroutines flow
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

//application {
//    mainClass.set("MainKt")
//}