plugins { id("com.android.application") }
android {
    namespace = "app.karanlikoda.android"
    compileSdk = 36
    defaultConfig {
        applicationId = "app.karanlikoda.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 300 + (System.getenv("GITHUB_RUN_NUMBER") ?: "1").toInt()
        versionName = "3.1"
        ndk { abiFilters += listOf("arm64-v8a") }
    }
    signingConfigs {
        create("okur") {
            storeFile = file("../okur.jks"); storePassword = "okurokur"; keyAlias = "okur"; keyPassword = "okurokur"
        }
    }
    buildTypes {
        getByName("release") { isMinifyEnabled = false; signingConfig = signingConfigs.getByName("okur") }
    }
    packaging { jniLibs { useLegacyPackaging = true } }   /* motor (libxul) sıkıştırılır: APK ~190 MB yerine çok daha küçük */
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
/* 3.0: GeckoView (Firefox motoru) uygulamanın içinde — WebView grafik çökmesinin çözümü. Sürüm: GeckoView 150 (arm64); 161+ compileSdk 37 ve AGP 9.1 istiyor. */
dependencies { implementation("org.mozilla.geckoview:geckoview-arm64-v8a:[150.0,151.0)") }
