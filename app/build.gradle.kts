import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.androidteamproject"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.androidteamproject"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        buildConfigField("String", "BASE_URL", "\"${localProperties.getProperty("BASE_URL") ?: ""}\"")
        buildConfigField("String", "LOCAL_BASE_URL", "\"${localProperties.getProperty("LOCAL_BASE_URL") ?: ""}\"")

        for (i in 1..5) {
            val apiKey = localProperties.getProperty("API_KEY$i") ?: ""
            buildConfigField("String", "API_KEY$i", "\"$apiKey\"")
        }
    }

    buildTypes {
        getByName("release") {
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
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("me.relex:circleindicator:2.1.6")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //DB연결
    implementation ("mysql:mysql-connector-java:5.1.49")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.4")
}
