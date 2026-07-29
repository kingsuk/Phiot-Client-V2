# PhiOT Client

Android companion app for the [PhiOT](https://phiot.phibasis.com) IoT platform. Use it to sign in, manage cloud-registered devices, provision hardware over Wi‑Fi, and send on/off commands through predefined datasets.

## What it does

PhiOT Client connects your phone to PhiOT cloud services and to PhiOT hardware during setup.

| Feature | Description |
|---------|-------------|
| **Account login** | Sign in with your PhiOT email and password. Sessions are stored securely on-device. |
| **Device dashboard** | View devices linked to your account, refresh the list, and remove devices you no longer need. |
| **Device setup** | While connected to a PhiOT device over Wi‑Fi, scan nearby networks and send credentials so the hardware can join your network. |
| **Dataset control** | Open a device to view its datasets and trigger **On** / **Off** actions. The app tracks remaining API calls for the current period. |

## Architecture

Modern native Android app built with current Google-recommended patterns:

- **Kotlin** + **Jetpack Compose** + **Material 3** (dynamic color, dark theme)
- **Single Activity** with **Navigation Compose** type-safe routes
- **MVVM** with `ViewModel`, `StateFlow`, and `hiltViewModel()`
- **Hilt** for dependency injection
- **Retrofit** + **OkHttp** + **kotlinx.serialization**
- **DataStore** for session persistence
- **Coil** for image loading
- **Splash Screen API** + edge-to-edge UI
- **Gradle Version Catalog** + **Kotlin DSL** build scripts

```
app/src/main/java/com/phiot/phiot_client/
├── MainActivity.kt
├── PhiOTApplication.kt          # @HiltAndroidApp
├── di/NetworkModule.kt          # Hilt network providers
├── data/
│   ├── model/
│   ├── local/TokenStore.kt
│   ├── remote/
│   └── PhiOTRepository.kt
└── ui/
    ├── login/
    ├── devices/
    ├── setup/
    ├── dataset/
    ├── main/
    ├── navigation/              # Type-safe routes + NavHost
    ├── session/SessionViewModel.kt
    └── theme/
```

## Requirements

- Android Studio Ladybug (2024.2.1) or newer recommended
- JDK 17
- Android SDK 35
- minSdk 24 (Android 7.0+)
- A PhiOT account and registered devices (for cloud features)
- Physical PhiOT hardware on its setup Wi‑Fi network (for provisioning)

## Getting started

### 1. Clone the repository

```bash
git clone https://github.com/kingsuk/Phiot-Client-V2.git
cd Phiot-Client-V2
```

### 2. Configure the Android SDK

Create `local.properties` in the project root (gitignored — do not commit):

```properties
sdk.dir=/path/to/your/Android/sdk
```

Open the project in Android Studio and let Gradle sync.

### 3. Build and run

```bash
./gradlew assembleDebug
```

Install the debug APK on a device or run directly from Android Studio.

## Configuration

API endpoints are defined in `AppConfig.kt`:

| Setting | Default | Purpose |
|---------|---------|---------|
| `CLOUD_BASE_URL` | `https://phiot.azurewebsites.net/api/` | PhiOT cloud REST API |
| `DEVICE_BASE_URL` | `http://192.168.4.22/` | Local device API during Wi‑Fi setup |

Cleartext HTTP is allowed only for the device setup IP via `network_security_config.xml`, not globally.

## Permissions

- `INTERNET` — cloud API access
- `ACCESS_NETWORK_STATE` — connectivity checks
- `ACCESS_WIFI_STATE` / `CHANGE_WIFI_STATE` — device setup
- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` — Wi‑Fi scanning on older Android versions

## Tech stack

| Component | Version |
|-----------|---------|
| Gradle | 8.9 |
| Android Gradle Plugin | 8.7.3 |
| Kotlin | 2.0.21 |
| Hilt | 2.52 |
| Compose BOM | 2024.10.01 |
| compileSdk / targetSdk | 35 |
| minSdk | 24 |

## Security notes

- **No API keys or secrets belong in this repository.** Authentication uses a bearer token returned at login and stored in DataStore on the device.
- Do **not** commit `local.properties`, keystores, or signing credentials.
- User passwords are sent only to the PhiOT auth endpoint at login and are not stored locally.
- Release builds use R8 minification and resource shrinking.

## Related links

- PhiOT portal: [phiot.phibasis.com](https://phiot.phibasis.com)
- Cloud API base: `https://phiot.azurewebsites.net/api/`

## License

No license file is included yet. Add one if you plan to distribute or open-source this project.
