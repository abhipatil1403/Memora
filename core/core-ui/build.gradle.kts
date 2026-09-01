plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.memora.core.ui"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    api(composeBom)

    api(libs.bundles.compose)
    api(libs.bundles.lifecycle)
    api(libs.androidx.activity.compose)

    debugApi(libs.androidx.compose.ui.tooling)

    implementation(project(":core:core-model"))
    implementation(project(":core:core-common"))

    implementation(libs.coil.compose)
}
