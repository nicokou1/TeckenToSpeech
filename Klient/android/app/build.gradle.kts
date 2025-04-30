plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // 2025-04-14
    // mimoza har lagt till plugin:
    kotlin("plugin.serialization") version "1.9.22" //kompilatorstöd för @Serializable
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
    implementation("androidx.compose.material:material-icons-extended:1.6.1")

    // 2025-04-30 Mimoza har lagt till:
    implementation("androidx.compose.material:material:1.6.1")

    // 2025-04-14
    // mimoza har lagt till följande beroenden:
    val ktorVersion = "2.3.4"

    // Ktor Client - kärnklasser för hantering av HTTP-anrop
    implementation("io.ktor:ktor-client-core:$ktorVersion")

    // "CIO" = Co-routine based I/O - nätverksmotor som skickar/tar emot nätverkstrafik
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // för att låta klienten driva innehållsförhandling
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    // möjliggör automatisk tolkning av och konvertering från JSON-objekt till kotlin-objekt
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // hanterar serialisering och deserialisering av JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}