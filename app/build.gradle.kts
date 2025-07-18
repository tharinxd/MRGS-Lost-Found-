plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "nz.school.mrgs.lostandfound"
    compileSdk = 34

    defaultConfig {
        applicationId = "nz.school.mrgs.lostandfound"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // This part is crucial for fixing the 'ActivityLoginBinding' error
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Standard Android Libraries
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // This is the Firebase Bill of Materials (BoM)
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))

    // Add the dependencies for the Firebase products you want to use
    // These lines fix the FirebaseAuth, GoogleAuthProvider, etc. errors
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // We will need this one later for the database
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    // This is for loading images from the internet, like the user's profile picture.
    implementation("com.github.bumptech.glide:glide:4.16.0")

}