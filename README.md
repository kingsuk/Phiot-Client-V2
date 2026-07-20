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

The app uses a modern single-activity Android stack:

- **Kotlin** with **Jetpack Compose** and **Material 3**
- **MVVM** (`ViewModel` + `StateFlow`)
- **Retrofit** + **OkHttp** + **kotlinx.serialization** for networking
- **DataStore** for session persistence
- **Navigation Compose** for screen flow

```
app/src/main/java/com/phiot/phiot_client/
├── MainActivity.kt              # Compose entry point
├── PhiOTApplication.kt          # App-wide dependencies
├── data/
│   ├── model/                   # API models
│   ├── local/                   # DataStore session storage
│   ├── remote/                  # Retrofit APIs + auth interceptor
│   └── PhiOTRepository.kt       # Data layer facade
└── ui/
    ├── login/                   # Login screen
    ├── devices/                 # Device list
    ├── setup/                   # Wi‑Fi provisioning
    ├── dataset/                 # Remote device control
    ├── main/                    # Navigation drawer shell
    ├── navigation/              # NavHost and routes
    └── theme/                   # Material 3 theme
```

## Requirements

- Android Studio Ladybug (2024.2.1) or newer recommended
- JDK 17
- Android SDK 35
- A PhiOT account and registered devices (for cloud features)
- Physical PhiOT hardware on its setup Wi‑Fi network (for provisioning)

## Getting started

### 1. Clone the repository

```bash
git clone https://github.com/kingsuk/Phiot-Client-V2.git
cd Phiot-Client-V2
```

### 2. Configure the Android SDK

Create `local.properties` in the project root (this file is gitignored and must not be committed):

```properties
sdk.dir=/path/to/your/Android/sdk
```

In Android Studio, open the project and let Gradle sync.

### 3. Build and run

```bash
./gradlew assembleDebug
```

Install the debug APK on a device or emulator, or run directly from Android Studio.

## Configuration

API endpoints are defined in `AppConfig.kt`:

| Setting | Default | Purpose |
|---------|---------|---------|
| `CLOUD_BASE_URL` | `https://phiot.azurewebsites.net/api/` | PhiOT cloud REST API |
| `DEVICE_BASE_URL` | `http://192.168.4.22/` | Local device API during Wi‑Fi setup |

`DEVICE_BASE_URL` is the default IP for PhiOT hardware in setup mode. Change it in `AppConfig.kt` if your device uses a different address.

Cleartext HTTP to the local device is enabled in the manifest so setup works on a typical IoT access-point network.

## Permissions

The app requests:

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
| compileSdk / targetSdk | 35 |
| minSdk | 21 |
| Compose BOM | 2024.10.01 |

## Security notes

- **No API keys or secrets belong in this repository.** Authentication uses a bearer token returned at login and stored in DataStore on the device.
- Do **not** commit `local.properties`, keystores, or signing credentials.
- User passwords are sent only to the PhiOT auth endpoint at login and are not stored locally.

## Related links

- PhiOT portal: [phiot.phibasis.com](https://phiot.phibasis.com)
- Cloud API base: `https://phiot.azurewebsites.net/api/`

## License

No license file is included yet. Add one if you plan to distribute or open-source this project.
