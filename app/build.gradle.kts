import java.net.URL

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    // Add the Dokka for Kotlin Documentation
    id("org.jetbrains.dokka") version "1.9.0"
}
tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    moduleName.set("ClientServerApp")
    moduleVersion.set(project.version.toString())
    outputDirectory.set(buildDir.resolve("dokka/$name"))
    failOnWarning.set(false)
    suppressObviousFunctions.set(true)
    suppressInheritedMembers.set(false)
    offlineMode.set(false)

    dokkaSourceSets.configureEach {
        suppress.set(false)
        displayName.set(name)
        documentedVisibilities.set(setOf(org.jetbrains.dokka.DokkaConfiguration.Visibility.PUBLIC))
        reportUndocumented.set(true)//
        skipEmptyPackages.set(true)
        skipDeprecated.set(true)//
        suppressGeneratedFiles.set(true)
        jdkVersion.set(8)
        languageVersion.set("1.7")
        apiVersion.set("1.7")
        noStdlibLink.set(false)
        noJdkLink.set(false)
        noAndroidSdkLink.set(true)
        //includes.from(project.files(), "packages.md", "extra.md")
        platform.set(org.jetbrains.dokka.Platform.Companion.DEFAULT)
        sourceRoots.from(file("src"))

        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            remoteUrl.set(URL("https://github.com/alprshn/Kotlin_Client_Server_App/tree/main/app/src"))
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLink {
            url.set(URL("https://kotlinlang.org/api/latest/jvm/stdlib/"))
            packageListUrl.set(
                rootProject.projectDir.resolve("stdlib.package.list").toURL()
            )
        }

        perPackageOption {
            matchingRegex.set(".*api.*")
            suppress.set(false)
            skipDeprecated.set(false)
            reportUndocumented.set(false)
            documentedVisibilities.set(
                setOf(
                    org.jetbrains.dokka.DokkaConfiguration.Visibility.PUBLIC,
                    org.jetbrains.dokka.DokkaConfiguration.Visibility.PRIVATE,
                    org.jetbrains.dokka.DokkaConfiguration.Visibility.PROTECTED,
                    org.jetbrains.dokka.DokkaConfiguration.Visibility.INTERNAL,
                    org.jetbrains.dokka.DokkaConfiguration.Visibility.PACKAGE
                )
            )
        }
    }
}



android {
    namespace = "com.example.client_server_app"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.client_server_app"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
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
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Scable Size Unit
    implementation("com.intuit.sdp:sdp-android:1.0.6")
    implementation("com.intuit.ssp:ssp-android:1.0.6")

    //Rounded ImageView
    implementation("com.makeramen:roundedimageview:2.3.0")
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    //Firebase
    implementation("com.google.firebase:firebase-messaging-ktx:23.2.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.7.1")

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    // https://firebase.google.com/docs/android/setup#available-libraries

    //MultiDex
    implementation("androidx.multidex:multidex:2.0.1")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.github.GrenderG:Toasty:1.5.2")

    //Email Verification
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth-ktx")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    //Strength Password Meter
    implementation("nu.aaro.gustav:passwordstrengthmeter:0.4")
}