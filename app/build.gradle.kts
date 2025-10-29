plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // تم تعطيل Firebase مؤقتًا
    // id("com.google.gms.google-services")
}

android {
    namespace = "com.example.vcamsx"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.vcamsx"
        minSdk = 24
        targetSdk = 34
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

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

repositories {
    google()
    mavenCentral()
    maven("https://api.xposed.info/")
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // بدائل مكتبة ijkplayer من JitPack (لتفادي الخطأ السابق)
    implementation("com.github.Bilibili:ijkplayer-java:0.8.8")
    implementation("com.github.Bilibili:ijkplayer-armv7a:0.8.8")
    implementation("com.github.Bilibili:ijkplayer-arm64:0.8.8")

    // Firebase تم تعطيله مؤقتًا
    // implementation("com.google.firebase:firebase-analytics-ktx:21.5.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
