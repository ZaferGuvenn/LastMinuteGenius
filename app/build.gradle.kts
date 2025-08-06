plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")

    id("com.google.gms.google-services")
}

android {
    namespace = "com.fastthinkerstudios.lastminutegenius"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fastthinkerstudios.lastminutegenius"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.benchmark.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Coroutine ve ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Retrofit (API çağrısı için)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    

    //di
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")

    implementation("com.google.dagger:hilt-android:2.57")
    ksp("com.google.dagger:hilt-compiler:2.57")

    // Compose ile ViewModel enjeksiyonu için
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")


    //ffmpeg ses videodan ses çıktısı yapmak için
    implementation("com.arthenica:ffmpeg-kit-full:6.0-2")
    implementation("com.arthenica:smart-exception-java:0.2.1")


    // google firebase
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-analytics")

    //room
    val room_version = "2.7.2"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:${room_version}")

    // gelişmiş icon paketi için
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // videoların snapshootlarını göstermek için
    implementation("io.coil-kt:coil-compose:2.6.0")


    // video ses dosyaları storage içine kaydet
    implementation("com.google.firebase:firebase-auth") // anonim auth için
    implementation("com.google.firebase:firebase-storage")
}