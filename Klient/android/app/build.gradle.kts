plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // 2025-04-14
    // mimoza har lagt till:
    kotlin("plugin.serialization") version "1.9.22" //så att man kan använda @serializable
}

android {
    namespace = "com.example.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = 34
        targetSdk = 35
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //FARZANEH IBRAHIMI 2025-04-16
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material:material-icons-extended:1.6.1")

    // 2025-04-14
    // mimoza har lagt till:
    val ktorVersion = "2.3.4"

    // Ktor Client – själva nätverksbiten
    implementation("io.ktor:ktor-client-core:$ktorVersion")

    // "CIO" = en typ av motor som hanterar nätverk – funkar för Android
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // För att läsa och skriva JSON-data
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // JSON-bibliotek
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}