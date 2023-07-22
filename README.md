# Android Unsplash gallery (sample project)

[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.8.x-blue.svg)](https://kotlinlang.org)
[![AGP](https://img.shields.io/badge/AGP-8.x-blue?style=flat)](https://developer.android.com/studio/releases/gradle-plugin)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-blue?style=flat)](https://gradle.org)

[![CodeFactor](https://www.codefactor.io/repository/github/maxmakarovdev/android-unsplash-gallery-sample/badge)](https://www.codefactor.io/repository/github/maxmakarovdev/android-unsplash-gallery-sample)
[![Codebeat Badge](https://codebeat.co/badges/c95841e4-1bd9-41ea-965b-1c451fe5697f)](https://codebeat.co/projects/github-com-maxmakarovdev-android-unsplash-gallery-sample-master)


## App description

Work in progress

![Work in progress](https://unsplash.com/photos/NoOrDKxUfzo/download?ixid=M3wxMjA3fDB8MXxzZWFyY2h8Mnx8dW5kZXIlMjBjb25zdHJ1Y3Rpb258ZW58MHx8fHwxNjg5NjA4Mjg1fDA&force=true&w=2400)

//todo add description/motivation & screenshots/video


## Tech stack

* Kotlin
   * Coroutines
   * Flow
* Architecture
  * MVVM + MVI + Repository Pattern
  * Single Activity
  * Multi-module
  * Android Architecture Components (ViewModel, Navigation, Lifecycle)
  * [WIP] Dagger/Hilt DI
* UI
  * [WIP] Jetpack Compose
  * Material Design 3 components
  * Coil
  * Lottie
  * BlurHash
* Data
  * Retrofit + OkHttp
  * Paging 3 library
  * Room
* Testing
  * [WIP] JUnit 5 + Mockk
* Code analysis
  * [WIP] Detekt


## Features Roadmap

- [x] Gallery screen
  - [x] Image list
  - [x] Data loading with pagination
  - [x] Search
- [x] Favorites screen
- [x] Fullscreen image view
  - [x] Add to favorites
  - [x] Share
  - [x] Download
  - [x] Zoomable image view
  - [x] Show image info details
- [ ] Settings screen


## Tech Roadmap

- [x] Load the image list with Retrofit, Coroutines/Flow and Paging 3 library
- [x] Room database for storing favorite photos
- [x] Multi-module
- [ ] Dagger/Hilt
- [ ] Unit tests
- [ ] Jetpack Compose


## Minor tasks and enhancements 

* Add info in README.md
* Handle multiple backstacks navigation https://developer.android.com/guide/navigation/backstack/multi-back-stacks
* Display a number of requests left (X-Ratelimit-Remaining)
* Error handling
* Gradle improvements
* Animations (Image opening, ...)
* Fonts


## Known issues

* ...


## Getting Started

To open this project:
* Clone the project via Git and open it via Android Studio

To launch the app:
* Register as a developer in Unsplash and create a demo project https://unsplash.com/oauth/applications/new
* Put a generated Access Key and Private Key to the `unsplash.keys` file in the root project directory:
   ```
   access_key="<your access key>"
   secret_key="<your secret key>"
   ```
* Run the app and enjoy!


## Links


## License

```
Designed and developed by 2023 maxmakarovdev (Maksim Makarov)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```