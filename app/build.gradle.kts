plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}


val geocode: String by project

android {
    namespace = "com.example.codriving"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.codriving"
        minSdk = 26 //24yy
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Lee la clave de API desde local.properties
        val geocodeApiKey = project.findProperty("geocode")

        // Configura la clave de API para la compilación
        buildConfigField("String", "GEOCODE_API_KEY", "\"${geocodeApiKey}\"")

    }


    buildTypes {
        release {

            // Configura la clave de API para la compilación de lanzamiento

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

    }


    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes + "META-INF/gradle/incremental.annotation.processors"
        }
        resources {
            jniLibs.pickFirsts.add("lib/**/libc++_shared.so")

        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.2")

    //Ciclo de vido de los viewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")

    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    implementation("androidx.compose.material3:material3:1.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("io.coil-kt:coil:2.5.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("io.coil-kt:coil-compose:2.5.0")


    //Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4") // <- this dependency is required

    //Carrucel
    implementation("com.google.accompanist:accompanist-pager:0.20.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.20.0")
    implementation("androidx.compose.ui:ui-util")

    //koin AsyncImage
    implementation(project.dependencies.platform("io.insert-koin:koin-bom:3.5.1"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-compose")

    //Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // FireBase
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))//Nos agrega las  ultimas versiones del resto de librerias de firebase
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging")

    //Google logIn
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.firebase:firebase-analytics")


    //Consumo de API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation("androidx.compose.material:material-icons-extended:1.6.0-alpha07")


    //Animate Icons
    implementation("com.airbnb.android:lottie-compose:6.0.0")


    //TOM TOM API palce finder
    //implementation ("com.tomtom.online:sdk-maps:6.0.0")
    //implementation ("com.tomtom.online:sdk-search:6.0.0")

}
