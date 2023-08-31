# Android Unsplash gallery (sample project)

[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.9.10-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![AGP](https://img.shields.io/badge/AGP-8.1.1-3DDC84?style=for-the-badge&logo=androidstudio)](https://developer.android.com/studio/releases/gradle-plugin)
[![Gradle](https://img.shields.io/badge/Gradle-8.3-999999?style=for-the-badge&logo=gradle)](https://gradle.org)
[![TargetSdk](https://img.shields.io/badge/Target%20SDK-34-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com/about/versions/14)


[![CodeFactor](https://www.codefactor.io/repository/github/maxmakarovdev/android-unsplash-gallery-sample/badge)](https://www.codefactor.io/repository/github/maxmakarovdev/android-unsplash-gallery-sample)
[![Codebeat Badge](https://codebeat.co/badges/c95841e4-1bd9-41ea-965b-1c451fe5697f)](https://codebeat.co/projects/github-com-maxmakarovdev-android-unsplash-gallery-sample-master)


## App description
<p>
  <img src="screenshots/screenshot_gallery.jpg" width="250" />
  <img src="screenshots/screenshot_image.jpg" width="250" />
  <img src="screenshots/screenshot_favorites.jpg" width="250" />
</p>

//todo description

## Tech stack

* [Kotlin](https://kotlinlang.org/)
   * [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
   * [Flow](https://kotlinlang.org/docs/flow.html)
* Architecture
  * [MVVM + MVI + Repository Pattern](https://developer.android.com/topic/architecture/recommendations)
  * [Single Activity](https://developer.android.com/topic/architecture/recommendations)
  * [Multi-module](https://developer.android.com/topic/modularization)
  * [Android Architecture components](https://developer.android.com/topic/libraries/architecture) ([ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel), [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/), [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle))
  * [Dagger/Hilt DI](https://developer.android.com/training/dependency-injection/hilt-android)
* UI
  * [WIP] [Jetpack Compose](https://developer.android.com/jetpack/compose)
  * [Material Design 3 components](https://m3.material.io/components) 
  * [Paging library v3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)
  * [Coil](https://github.com/coil-kt/coil)
  * [Lottie](http://airbnb.io/lottie)
  * [BlurHash](https://blurha.sh/)
* Data
  * [Retrofit](https://square.github.io/retrofit/)
  * [OkHttp](https://square.github.io/okhttp/)
  * [Room](https://developer.android.com/jetpack/androidx/releases/room)
* Testing
  * [WIP] [JUnit 5](https://junit.org/junit5/) + [Mockk](https://mockk.io/)
* Compiling and building
  * [KSP](https://kotlinlang.org/docs/ksp-overview.html)
* Code analysis
  * [WIP] [Detekt](https://github.com/arturbosch/detekt)


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
- [x] Room database for storing favorite images
- [x] Multi-module
- [x] Dagger/Hilt
- [ ] Unit tests
- [ ] Jetpack Compose


## Minor tasks and enhancements 

* Handle multiple backstacks navigation https://developer.android.com/guide/navigation/backstack/multi-back-stacks
* Display a number of requests left (X-Ratelimit-Remaining)
* Error handling
* Gradle improvements
* Animations (Image opening, ...)
* Fonts
* Add more info in the README.md


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