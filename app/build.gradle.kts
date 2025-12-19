plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.services)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp"
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.glide)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(platform("com.google.firebase:firebase-bom:33.1.1")) // Pastikan BOM sudah ada
    implementation("com.google.firebase:firebase-storage-ktx")
    ksp(libs.glide.ksp)
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("org.tensorflow:tensorflow-lite:2.17.0") // Library TFLite Standar
    implementation("org.tensorflow:tensorflow-lite-select-tf-ops:2.16.1") // Library ini berisi "kamus" untuk operasi teks (Regex, dll)
    implementation("org.tensorflow:tensorflow-lite-support:0.5.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.5.0")
}