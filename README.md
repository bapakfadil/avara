# Avara - Minimalist Note-Taking App

Avara is a modern, minimalist note-taking application for Android, built entirely with Jetpack Compose and integrated with Firebase. It provides a clean, seamless, and intuitive user experience for creating, managing, and accessing notes anytime, anywhere.

## ✨ Features

- **User Authentication**: Secure email and password login and registration powered by Firebase Authentication.
- **Create & Manage Notes**: Easily create, view, edit, and delete notes in a clean, modern interface.
- **Real-time Sync**: All notes are synchronized in real-time across devices using Cloud Firestore.
- **Quote of the Day**: Features an inspirational quote on the home screen fetched from an external API.

## 🛠️ Technology Stack & Key Dependencies

Avara is built using a modern Android tech stack, ensuring it is robust, scalable, and easy to maintain.

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (`androidx.compose.bom:2024.09.00`) - Android's modern, declarative UI toolkit for building native UI.
- **Architecture**: MVVM (Model-View-ViewModel).
- **Backend & Database**: [Firebase](https://firebase.google.com/) (`com.google.firebase:firebase-bom:33.7.0`)
  - **Authentication**: Firebase Authentication (`firebase-auth-ktx`) for managing user sessions.
  - **Database**: Cloud Firestore (`firebase-firestore-ktx`) for real-time note storage and synchronization.
  - **Crash Reporting**: Firebase Crashlytics (`firebase-crashlytics-buildtools:3.0.6`) for monitoring stability.
- **State Management & Lifecycle**:
  - `androidx.lifecycle:lifecycle-runtime-ktx:2.9.4`
  - `androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4`
- **Asynchronous Programming**: Kotlin Coroutines for managing background threads and asynchronous tasks.
- **Networking**:
  - [Retrofit](https://square.github.io/retrofit/) (`com.squareup.retrofit2:retrofit:2.9.0`) for making HTTP requests to fetch the daily quote.
  - [Gson](https://github.com/google/gson) (`com.google.code.gson:gson:2.10.1`) for JSON serialization/deserialization.
- **Language**: [Kotlin](https://kotlinlang.org/) (`androidx.core:core-ktx:1.17.0`).
- **Navigation**: Jetpack Compose Navigation for handling screen transitions within the app.

## ⚙️ Setup and Installation

To get the Avara project up and running on your local machine, follow these steps:

**1. Clone the Repository**

```
bash git clone https://github.com/your-username/avara.git cd avara
```

**2. Set up Firebase**

- Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
- Add an Android app to your Firebase project with the package name `com.project.avara`.
- Follow the setup instructions to download the `google-services.json` file.
- Place the downloaded `google-services.json` file in the `avara/app/` directory.
- In the Firebase Console, navigate to **Build** and enable:
  - **Authentication** (with the Email/Password sign-in provider).
  - **Firestore Database** (create a new database in test or production mode).

**3. Build the Project**

- Open the project in a recent version of Android Studio.
- Let Gradle sync the project dependencies.
- Build and run the app on an Android emulator or a physical device.

## 🚀 Future Roadmap

This MVP is a solid foundation, but there's always room for improvement. Here are some features planned for future releases:

- **Phase 1:**

  - [ ] Implement `Snackbar` for error handling.
  - [ ] Improve keyboard handling on input screens using `Modifier.imePadding()`.
  - [ ] Add subtle animations for screen transitions using `AnimatedVisibility`.

- **Phase 2:**

  - [ ] **Search Functionality**: Add a search bar to the home screen to filter notes by title or content.
  - [ ] **Offline Support**: Enable Firestore's built-in offline persistence for full offline capabilities.
  - [ ] **Settings Screen**: Create a dedicated settings screen for logout, theme selection (dark/light mode), and account management.

- **Phase 3:**
  - [ ] **Unit & UI Tests**: Write comprehensive tests for ViewModels and UI components to ensure code quality and stability.
  - [ ] **Dependency Injection with Hilt**: Formally integrate Hilt for robust dependency management.
  - [ ] **Release Preparation**: Design an app icon, take store screenshots, and prepare the app for a Google Play Store release.

## 🤝 Contributing

Contributions are welcome! If you have ideas for new features, find a bug, or want to improve the code, please feel free to open an issue or submit a pull request.
