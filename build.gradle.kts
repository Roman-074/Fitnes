// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.2.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
        classpath ("com.likethesalad.android:android-buddy-plugin:1.0.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }

}


tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}