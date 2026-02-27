# Rental Proofer

Rental Proofer is an Android application built in Kotlin for storing and managing rental proof documents. It provides a convenient way for tenants and landlords to keep records of rental-related proofs (e.g., payment receipts, inspection reports, lease agreements) directly on their Android device.

## Features

- Store and manage rental proof documents
- Lightweight, offline-first Android app
- Material Design UI

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| Platform | Android |
| Build System | Gradle (Kotlin DSL) |
| UI | Material Design (AndroidX) |
| Min SDK | 24 (Android 7.0 Nougat) |
| Target SDK | 36 |

## Prerequisites

- Android Studio (latest stable recommended)
- JDK 11+
- Android SDK with API level 24 or higher installed

## Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/florianbodr/Rental_Proofer.git
   cd Rental_Proofer
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select **File > Open** and navigate to the cloned directory

3. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on a device or emulator**
   - Connect an Android device with USB debugging enabled, or start an emulator
   - Click **Run** in Android Studio, or run:
     ```bash
     ./gradlew installDebug
     ```

## Project Structure

```
Rental_Proofer/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   └── res/            # Resources (layouts, drawables, strings…)
│   │   ├── test/               # Unit tests
│   │   └── androidTest/        # Instrumented tests
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Running Tests

- **Unit tests**
  ```bash
  ./gradlew test
  ```

- **Instrumented tests** (requires a connected device or running emulator)
  ```bash
  ./gradlew connectedAndroidTest
  ```

## Dependencies

| Dependency | Purpose |
|-----------|---------|
| `androidx.core:core-ktx` | Kotlin extensions for Android core APIs |
| `androidx.appcompat:appcompat` | Backwards-compatible AppCompat support |
| `com.google.android.material:material` | Material Design components |
| `junit:junit` | Unit testing |
| `androidx.test.ext:junit` | AndroidX JUnit extensions |
| `androidx.test.espresso:espresso-core` | UI testing |

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -m 'Add my feature'`)
4. Push to the branch (`git push origin feature/my-feature`)
5. Open a Pull Request

## License

This project is open source. See the repository for license details.
