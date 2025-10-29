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
        gradlePluginPortal()
        mavenCentral()
        // مصدر Xposed
        maven("https://api.xposed.info/")
        // jcenter لم يعد مدعوم، لكن سنبقيه للاحتياط
        jcenter()
    }
}

rootProject.name = "VCAMSX"
include(":app")
