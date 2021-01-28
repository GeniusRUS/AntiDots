import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.jfrog.bintray")
    id("com.github.dcendents.android-maven")
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}

extra.apply{
    set("bintrayRepo", "AntiDots")
    set("bintrayName", "com.geniusrus.AntiDots")
    set("libraryName", "antidots")
    set("publishedGroupId", "com.geniusrus.antidots")
    set("artifact", "antidots")
    set("libraryVersion", "1.0.0")
    set("libraryDescription", "Android library for displaying dots with some scroll containers")
    set("siteUrl", "https://github.com/GeniusRUS/AntiDots")
    set("gitUrl", "https://github.com/GeniusRUS/AntiDots.git")
    set("developerId", "GeniusRUS")
    set("developerName", "Viktor Likhanov")
    set("developerEmail", "Gen1usRUS@yandex.ru")
    set("licenseName", "The Apache Software License, Version 2.0")
    set("licenseUrl", "http://www.apache.org/licenses/LICENSE-2.0.txt")
    set("allLicenses", arrayOf("Apache-2.0"))
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(30)
        versionCode = 1
        versionName = extra.get("libraryVersion") as String
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    lintOptions {
        isAbortOnError = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}