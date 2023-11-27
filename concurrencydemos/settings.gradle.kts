pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "concurrency-demos"
include(":ui")
include(":data")
include(":scenarios_jvm_coroutine_rx")
include(":scenarios_android_coroutine_rx")
