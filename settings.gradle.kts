rootProject.name = "HabitGoldMobile"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven("https://maven.juspay.in/jp-build-packages/hypersdk-asset-download/releases/") {
            mavenContent {
                includeGroupAndSubgroups("in.juspay")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven("https://maven.juspay.in/jp-build-packages/hyper-sdk/") {
            mavenContent {
                includeGroupAndSubgroups("in.juspay")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
