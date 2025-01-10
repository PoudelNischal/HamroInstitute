plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services) // This applies the Google Services plugin
}

android {
    namespace = "com.example.merainstitue"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.merainstitue"
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

    viewBinding {
        enable = true
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("com.cloudinary:cloudinary-android:3.0.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.navigation:navigation-fragment:2.8.5")
    implementation("androidx.navigation:navigation-ui:2.8.5")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.airbnb.android:lottie:6.6.2")

//    Google Play and payment intregation
    implementation("com.google.android.gms:play-services-wallet:19.4.0")
    implementation("com.google.android.gms:play-services-pay:16.5.0")
    implementation("com.android.billingclient:billing:7.1.1")
    implementation("com.google.android.gms:play-services-base:18.5.0")

    implementation("com.stripe:stripe-android:21.3.0")
    implementation("com.stripe:stripe-java:28.2.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.8.8")

    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.ui)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth-ktx:23.1.0") // KTX version for Firebase Authentication
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.1") // KTX version for Firestorm
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation(libs.firebase.messaging) // KTX version for Realtime Database


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
