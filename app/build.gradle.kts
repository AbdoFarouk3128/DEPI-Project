plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.absolutecinema"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.absolutecinema"
        minSdk = 29
        targetSdk = 36
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
//    implementation(libs.firebase.auth.ktx)
//    implementation(libs.androidx.navigation.compose.jvmstubs)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation(libs.glide)
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation(platform("androidx.compose:compose-bom:2025.11.00"))
    implementation("androidx.navigation:navigation-compose:2.9.6")
    implementation(libs.compose.bom.v20251001)
    implementation("androidx.compose.material:material-icons-extended:1.7.7")
    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    //navigation
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("com.google.android.gms:play-services-base:18.5.0")
    // Firebase Storage for profile pictures
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.airbnb.android:lottie-compose:4.0.0")
    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")

}