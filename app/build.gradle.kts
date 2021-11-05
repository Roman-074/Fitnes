plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("android-buddy")
}



android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "hedgehog.tech.fitnes"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
        applicationVariants.all {
            outputs.all {
                if (name.contains("release"))
                    (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                        "../../apk/AR14-kronos-${applicationId}-v${versionName}(${versionCode}).apk"
            }
        }
        resValue("string", "AppMetricaKey", "key")
        buildConfigField("String", "AppMetricaKey", "\"key\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }


    lintOptions {
        isCheckReleaseBuilds = false
        isAbortOnError = false
    }

    dexOptions {
        incremental = true
        preDexLibraries = false
        javaMaxHeapSize = "4g"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.10")
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    //For dinamycal dimens in xml layouts (sp,dp)
    implementation(Dependencies.Dimens.sdp)
    implementation(Dependencies.Dimens.ssp)
    //Rounded ImageViews
    implementation("com.makeramen:roundedimageview:2.3.0")

    // Navigation Components
    implementation(Dependencies.NavigationComponents.ui)
    implementation(Dependencies.NavigationComponents.fragmentKtx)

    //asynchronous work with threads
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.1.1")
    // Vuew models for MVVM architecture
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")

    // Glide for load images into different views
    implementation("com.github.bumptech.glide:glide:4.11.0")
    kapt("com.github.bumptech.glide:compiler:4.11.0")

    // Easy obtaining of permissions
    implementation("pub.devrel:easypermissions:3.0.0")

    // Timber for logging
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Dagger for dependencies injection
    implementation("com.google.dagger:hilt-android:2.38.1")
    kapt("com.google.dagger:hilt-compiler:2.38.1")


    //Room for easy use of SQLdatabase
    implementation("androidx.room:room-runtime:2.3.0")
    kapt("androidx.room:room-compiler:2.3.0")
    implementation("androidx.room:room-ktx:2.3.0")


    //Flex recycleView to creating custom recycleViews
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    //image cropper to crop user"s avatar
    implementation("androidx.activity:activity-ktx:1.3.0-beta01")
    implementation("com.github.CanHub:Android-Image-Cropper:3.1.3")

    //trasnparent status bar to made transuluted status ar
    implementation("com.github.niorgai:StatusBarCompat:2.3.3") {
        exclude("androidx.appcompat:appcompat")
        exclude("com.google.android.material:material")
    }
    //lottie animations for animations in views
    val lottieVersion = "3.4.0"
    implementation("com.airbnb.android:lottie:$lottieVersion")
    //progress bar for expand progressbar functionality
    implementation("com.mikhaellopez:circularprogressbar:3.1.0")
    //Google json converter to convert json from files
    implementation("com.google.code.gson:gson:2.8.7")

    // Workmanager to scheduling notifications
    implementation("androidx.work:work-runtime-ktx:2.6.0-beta01")
    implementation("androidx.work:work-runtime:2.6.0-beta01")


}

kapt {
    correctErrorTypes = true
}