plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.atomicrobot.beantown"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.atomicrobot.beantown"
        minSdk = 23
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
        buildConfig = true
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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.testing)

    // Paging3
    implementation(libs.bundles.paging)

    // Ktor
    implementation(libs.bundles.ktor.client)
    implementation(libs.ktor.serialization)

    //Koin
    implementation(libs.bundles.koin)
    ksp(libs.koin.ksp.compiler)
    // KSerialization
    implementation(libs.kotlinx.serialization.json)
    // Room
    implementation(libs.bundles.room)
    // KSP
    ksp(libs.androidx.room.compiler)
    // Coil
    implementation(libs.bundles.coil)
    // Palette
    implementation(libs.androidx.palette.ktx)
    // Timber
    implementation(libs.timber)
    // Splash
    implementation(libs.androidx.core.splashscreen)

    testImplementation(libs.square.turbine)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockk)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(kotlin("test"))
    androidTestImplementation(kotlin("test"))
}