import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}