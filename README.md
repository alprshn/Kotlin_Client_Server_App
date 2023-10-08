# Kotlin Client-Server Chat App

This application is an example of a Chat App built using Firebase. This README file provides information about the configuration and key components of your application. It will help you when developing or launching your application.

## Project Structure

Your project consists of the following components:

- `app` folder: Contains your application's code and resources.
- `build.gradle` file: Used for Gradle dependencies and project settings.
- `README.md` file: This document, which provides a description of your project.

## Technologies and Libraries Used

The main technologies and libraries used in this project include:

- **Firebase**: We use Firebase for user authentication and database functionalities.
- **AndroidX Libraries**: Various AndroidX libraries are used for Android app development.
- **Retrofit**: We use Retrofit for making HTTP requests and communicating with APIs.
- **Toasty**: Toasty library is used for displaying informative messages to the user.
- **Password Strength Meter**: The Password Strength Meter library is used for checking password strength.

## Gradle plugins and dependencies
```groovy
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    // Add the Dokka for Kotlin Documentation
    id("org.jetbrains.dokka") version "1.9.0"
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Other dependencies...

    // Email Verification
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth-ktx")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Strength Password Meter
    implementation("nu.aaro.gustav:passwordstrengthmeter:0.4")
}```

## Installation

To run the application in a local development environment, you can follow these steps:

1. Clone this repository to your computer: `git clone https://github.com/alprshn/Kotlin_Client_Server_App.git`

2. Open Android Studio and import the project.

3. Create a Firebase account and configure a Firebase project for your application. Add Firebase connection settings to the `google-services.json` file.

4. Enable and configure Firebase Authentication and Firestore for your project.

5. Properly configure Firebase project settings in the `build.gradle` file.

6. Once Firebase setup is complete, launch your application.

## Contributing

If you wish to contribute, please take a look at our contribution guide, and feel free to open issues and requests.

## License

This project is licensed under the MIT License. For more information, please see the [LICENSE](LICENSE) file.

---

We hope this README file helps you understand how to set up and use your application. Happy coding!
