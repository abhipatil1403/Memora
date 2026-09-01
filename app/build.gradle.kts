plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
  alias(libs.plugins.google.services)
}

android {
    namespace = "com.memora"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.memora"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
      compose = true
      aidl = false
      buildConfig = false
      shaders = false
    }

    packaging {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)

  // Core Android dependencies
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.splashscreen)
  implementation(libs.timber)
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.auth)

  // Core Modules
  implementation(project(":core:core-model"))
  implementation(project(":core:core-common"))
  implementation(project(":core:core-ui"))
  implementation(project(":core:core-network"))
  implementation(project(":core:core-database"))
  implementation(project(":core:core-firebase"))
  implementation(project(":core:core-datastore"))

  // Feature Modules
  implementation(project(":feature:feature-splash"))
  implementation(project(":feature:feature-onboarding"))
  implementation(project(":feature:feature-auth"))
  implementation(project(":feature:feature-home"))
  implementation(project(":feature:feature-capture"))
  implementation(project(":feature:feature-processing"))
  implementation(project(":feature:feature-result"))
  implementation(project(":feature:feature-library"))
  implementation(project(":feature:feature-memory-detail"))
  implementation(project(":feature:feature-collections"))
  implementation(project(":feature:feature-search"))
  implementation(project(":feature:feature-reminders"))
  implementation(project(":feature:feature-profile"))
  implementation(project(":feature:feature-settings"))

  // Hilt
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
  implementation(libs.hilt.navigation.compose)

  // Arch Components
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Compose
  implementation(libs.bundles.compose)
  // Tooling
  debugImplementation(libs.androidx.compose.ui.tooling)
  // Instrumented tests
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  // Local tests: jUnit, coroutines, Android runner
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)

  // Instrumented tests: jUnit rules and runners
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.espresso.core)

  // Navigation
  implementation(libs.bundles.navigation)
}
