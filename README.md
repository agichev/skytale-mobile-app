# Skytale Mobile Public

Skytale Mobile Public is the Android client source tree prepared for public review, security analysis, and permission-based collaboration.

This repository contains only the mobile application. The production backend, deployment assets, signing material, and server-side operational code are intentionally excluded.

## Repository Scope

- `app/`: Android application module
- `gradle/`, `gradlew`, `gradlew.bat`: Gradle wrapper and version catalog
- `LICENSE`: source-available license for this public mobile code drop
- `SECURITY.md`: responsible disclosure guidance
- `NOTICE`: attribution and provenance notice

## Important Boundaries

- This repository does not include the backend server.
- This repository does not include production secrets, signing keys, or release credentials.
- The default API and WebSocket endpoints are placeholders and must be configured locally before use.

## Configure Endpoints

Set the following Gradle properties in `~/.gradle/gradle.properties` or your local project `gradle.properties`:

```properties
SKYTALE_API_BASE_URL=https://your-server.example/api/v1/
SKYTALE_WEBSOCKET_URL=wss://your-server.example/api/v1/realtime
```

Both values should point to an HTTPS/WSS deployment you control or are authorized to test.

## Build

```bash
./gradlew assembleDebug
```

On Windows:

```powershell
.\gradlew.bat assembleDebug
```

## Security Notes

- Sensitive session data is stored with `EncryptedSharedPreferences`.
- Local message content cached on device is encrypted with an Android Keystore-backed key.
- Cleartext traffic is disabled.
- This public tree intentionally avoids embedding a production certificate pin or production host.

## Intended Use

This publication is meant for:

- independent code review
- security research
- auditing and verification
- permission-based contribution and integration work

Please read [LICENSE](LICENSE) before copying, redistributing, integrating, or publishing derivatives.

## Attribution

If you receive written permission to use or redistribute this code, attribution to the original Skytale source must be preserved as described in [NOTICE](NOTICE) and [LICENSE](LICENSE).
