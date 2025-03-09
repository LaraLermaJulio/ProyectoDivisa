plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
    kotlin("kapt") // ⚠️ necesario para ROOM
}

android {
    namespace = "com.example.divisa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.divisa"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }

    dependencies {
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.activity:activity-compose:1.8.2")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

        implementation("androidx.compose.ui:ui:1.5.4")
        implementation("androidx.compose.material3:material3:1.2.1")
        implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
        implementation("androidx.activity:activity-compose:1.8.2")

        implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

        // Retrofit y Serialización
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
        implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

        // Room (corrección con KAPT)
        implementation("androidx.room:room-runtime:2.6.1")
        kapt("androidx.room:room-compiler:2.6.0")
        implementation("androidx.room:room-ktx:2.6.0")

        // Coil
        implementation("io.coil-kt:coil-compose:2.4.0")

        // ViewModel Compose
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

        // WorkManager (opcional, por si lo usas)
        implementation("androidx.work:work-runtime-ktx:2.9.0")

        // Test
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")

        // Debugging
        debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
        debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")

        // Coil
        implementation("io.coil-kt:coil-compose:2.4.0")

        // Chart (si lo necesitas)
        implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    }

    // KAPT para ROOM
    apply(plugin = "kotlin-kapt")
    dependencies {
        kapt("androidx.room:room-compiler:2.6.0")
    }

}
