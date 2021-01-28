import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.config.KotlinCompilerVersion

buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    id("com.jfrog.bintray")
    id("com.android.library")
    id("maven-publish")
    kotlin("android")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0.0"
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

group = "com.geniusrus.view"
version = android.defaultConfig.versionName.toString()

dependencies {
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}

tasks {
    register("androidSourcesJar", Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }
}

publishing {
    publications {
        register<MavenPublication>("AntiDotsLibrary") {
            artifactId = "antidots"

            afterEvaluate { artifact(tasks.getByName("bundleReleaseAar")) }
            artifact(tasks.getByName("androidSourcesJar"))

            pom {
                name.set("AntiDots")
                description.set("Android library for displaying dots with some scroll containers")
                url.set("https://github.com/GeniusRUS/AntiDots")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("GeniusRUS")
                        name.set("Viktor Likhanov")
                        email.set("Gen1usRUS@yandex.ru")
                    }
                }
                scm {
                    connection.set("git://github.com/GeniusRUS/AntiDots.git")
                    developerConnection.set("git://github.com/GeniusRUS/AntiDots.git")
                    url.set("https://github.com/GeniusRUS/AntiDots")
                }

                // Saving external dependencies list into .pom-file
                withXml {
                    fun groovy.util.Node.addDependency(dependency: Dependency, scope: String) {
                        appendNode("dependency").apply {
                            appendNode("groupId", dependency.group)
                            appendNode("artifactId", dependency.name)
                            appendNode("version", dependency.version)
                            appendNode("scope", scope)
                        }
                    }

                    asNode().appendNode("dependencies").let { dependencies ->
                        // List all "api" dependencies as "compile" dependencies
                        configurations.api.get().allDependencies.forEach {
                            dependencies.addDependency(it, "compile")
                        }
                        // List all "implementation" dependencies as "runtime" dependencies
                        configurations.implementation.get().allDependencies.forEach {
                            dependencies.addDependency(it, "runtime")
                        }
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "bintray"
            credentials {
                username = gradleLocalProperties(rootDir).getProperty("bintray.user")
                password = gradleLocalProperties(rootDir).getProperty("bintray.apikey")
            }
            url = uri("https://api.bintray.com/maven/geniusrus/AntiDots/$group/;publish=1")
        }
    }
}