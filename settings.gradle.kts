pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "Memora"

// App module
include(":app")

// Core modules
include(":core:core-model")
include(":core:core-common")
include(":core:core-ui")
include(":core:core-network")
include(":core:core-database")
include(":core:core-firebase")
include(":core:core-datastore")
include(":core:core-testing")

// Feature modules
include(":feature:feature-splash")
include(":feature:feature-onboarding")
include(":feature:feature-auth")
include(":feature:feature-home")
include(":feature:feature-capture")
include(":feature:feature-processing")
include(":feature:feature-result")
include(":feature:feature-library")
include(":feature:feature-memory-detail")
include(":feature:feature-collections")
include(":feature:feature-search")
include(":feature:feature-reminders")
include(":feature:feature-profile")
include(":feature:feature-settings")
