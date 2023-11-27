plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}
dependencies{
    implementation("org.json:json:20231013")

}