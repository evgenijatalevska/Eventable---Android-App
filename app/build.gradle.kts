plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.eventable"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.eventable"
        minSdk = 24
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Основни Android и Compose библиотеки
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Тестирање
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ==========================================
    // ТВОИТЕ Firebase и Facebook БИБЛИОТЕКИ (ЗАДОЛЖИТЕЛНИ)
    // ==========================================

    // 1. Firebase BoM (Оваа стабилна верзија ги контролира сите Firebase модули подолу)
    implementation(platform("com.google.firebase:firebase-bom:34.13.0"))

    // 2. Firebase Authentication (Точна имплементација)
    implementation("com.google.firebase:firebase-auth")

    // Поддршка за Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Facebook Login SDK (Задолжително од барањата)
    implementation("com.facebook.android:facebook-login:17.0.0")

    // 3. Firebase Firestore (База на податоци)
    implementation("com.google.firebase:firebase-firestore")

    // 4. Firebase Messaging (Нотификации)
    implementation("com.google.firebase:firebase-messaging")

    // 5. Firebase Analytics (Аналитика)
    implementation("com.google.firebase:firebase-analytics")
}