pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.bilibili.com/repository/public")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.bilibili.com/repository/public")
    }
}

rootProject.name = "VirtuCam3"
include(":app")
