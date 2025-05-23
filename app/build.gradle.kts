plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.stepup"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.stepup"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // ViewModel
    implementation("androidx.activity:activity-ktx:1.4.0")

    // Thư viện khác
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("com.tbuonomo:dotsindicator:5.0")

    // Network & Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.12.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")

    //load ảnh với Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

//    implementation ("com.google.android.material:material:1.9.0")

    // https://mvnrepository.com/artifact/com.google.android.material/material
    implementation("com.google.android.material:material:1.12.0")

    implementation("com.google.android.gms:play-services-auth:20.7.0") // dung de dang nhap google

    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0") // thư viện lam hieu ung load lai du lieu
  	implementation ("com.google.android.gms:play-services-maps:18.2.0")  // thu vien su dung google map
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.libraries.places:places:3.5.0")
    implementation ("com.github.ismaeldivita:chip-navigation-bar:1.4.0")

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")

    // https://mvnrepository.com/artifact/com.jakewharton.threetenabp/threetenabp
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.9")
//    vi LocalDate chi ho tro API > 26 ma ung dung dang de minAPI 24 nen can su dung locale Date cua threetenabp


//    implementation ("com.agrawalsuneet.androidlibs:dotsloader:1.4")

    // https://mvnrepository.com/artifact/com.github.worker8/radiogroupplus
//    implementation("com.github.worker8:radiogroupplus:1.0.1")

    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // Bo tròn góc ảnh
    implementation("com.makeramen:roundedimageview:2.3.0")
}