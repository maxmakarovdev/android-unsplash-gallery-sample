# Android Unsplash gallery (sample project)

[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.8.x-blue.svg)](https://kotlinlang.org)
[![AGP](https://img.shields.io/badge/AGP-8.x-blue?style=flat)](https://developer.android.com/studio/releases/gradle-plugin)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-blue?style=flat)](https://gradle.org)

[![CodeFactor](https://www.codefactor.io/repository/github/maxmakarovdev/android-unsplash-gallery-sample/badge)](https://www.codefactor.io/repository/github/maxmakarovdev/android-unsplash-gallery-sample)
[![Codebeat Badge](https://codebeat.co/badges/c95841e4-1bd9-41ea-965b-1c451fe5697f)](https://codebeat.co/projects/github-com-maxmakarovdev-android-unsplash-gallery-sample-master)


## App description

//todo add description/motivation & screenshots/video


## Tech stack

* Kotlin
   * Coroutines
   * Flow
* Architecture
  * MVVM + MVI + Repository Pattern
  * Single Activity
  * [WIP] Multi-module
  * Android Architecture Components (ViewModel, Navigation, Lifecycle)
  * [WIP] Dagger/Hilt DI
* UI
  * [WIP] Jetpack Compose
  * [WIP] Material Design 3 components
  * Coil
  * [WIP] Lottie
  * BlurHash
* Data
  * Retrofit + OkHttp
  * Paging 3 library
  * Room
* Testing
  * [WIP] JUnit 5 + Mockk
* [WIP] Code analysis


## Features Roadmap

- [x] Gallery screen (Image list, loading data with pagination, search)
- [] Favorites screen
- [] Fullscreen image view (share, download, add to favs, show details about the image)
- [] Settings screen


## Tech Roadmap

- [x] Load the image list with Retrofit, Coroutines and Paging 3 library
- [x] Room database for storing favorite photos
- [] Multi-module
- [] Dagger/Hilt
- [] Unit tests
- [] Jetpack Compose


## Minor tasks and enhancements 

* Add info in README.md
* Display a number of requests left (X-Ratelimit-Remaining)
* Error handling
* Gradle improvements


## Known issues

* ...


## Getting Started

To open this project:
* Clone the project via Git and open it via Android Studio

To launch the app:
* Register as a developer in Unsplash and create a demo project https://unsplash.com/oauth/applications/new
* Put a generated Access Key and Private Key to the `app/unsplash.keys` file:
   ```
   access_key="<your access key>"
   secret_key="<your secret key>"
   ```
* Run the app and enjoy!


## Links


## Licenses
