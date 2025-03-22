import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "net.blwsmartware.qrcodescanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "net.blwsmartware.qrcodescanner"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val formattedDate = SimpleDateFormat("MMM.dd.yyyy").format(Date())
        base.archivesName = "FileRecovery-v$versionName($versionCode)_${formattedDate}"
    }

    signingConfigs {
        create("release") {
            keyAlias = "BlackWolves"
            keyPassword = "blackwolves1"
            storeFile = rootProject.file("keystore/blackwolves.jks")
            storePassword = "blackwolves1"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
           signingConfig = signingConfigs.getByName("release")
        }
    }
    flavorDimensions.add("version")
    productFlavors {
        create("dev") {
            applicationId = "net.blwsmartware.qrcodescanner"
            buildConfigField("boolean", "build_debug", "true")
        }

        create("product") {
            applicationId = "net.blwsmartware.qrcodescanner"
            buildConfigField("boolean", "build_debug", "false")
        }
    }
    bundle {
        language {
            enableSplit = false
        }
    }


    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.review.ktx)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.play.services.mlkit.barcode.scanning)
    implementation(libs.androidx.browser)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":lib"))



    implementation("com.google.zxing:core:3.5.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    val cameraxVersion = "1.5.0-alpha01"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-video:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-extensions:$cameraxVersion")

    implementation("com.google.guava:guava:31.1-jre")

    implementation("com.google.mlkit:image-labeling:17.0.8")
    implementation("com.google.mlkit:text-recognition:16.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    implementation("org.jsoup:jsoup:1.16.2")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")

    implementation("com.google.android.play:asset-delivery-ktx:2.2.2")
    implementation("com.google.android.play:review-ktx:2.0.1")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    val lottieVersion = "3.4.0"
    implementation("com.airbnb.android:lottie:$lottieVersion")

    val roomVersion = "2.6.1"
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-rxjava3:$roomVersion")
    implementation("androidx.room:room-guava:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")



    implementation("com.google.firebase:firebase-bom:30.1.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-crashlytics:19.3.0")
    implementation("com.android.billingclient:billing:7.1.1")
    implementation("com.google.firebase:firebase-analytics:22.1.2")
    implementation("com.google.firebase:firebase-messaging:24.1.0")

    implementation("com.google.firebase:firebase-config:21.2.0")

}