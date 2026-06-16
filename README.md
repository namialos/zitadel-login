# Zitadel Login (KMP)

Persian RTL native OAuth2 client for ZITADEL at `https://zitadel.pxteam.ir`.

## Use this redirect URI in ZITADEL

Register this **exact** redirect URI on native client **`373632460690310718`**:

**`org.example.zitadellogin://oauth/callback`**

## Native client ID

The official native client ID is defined once in `AuthConfig.CLIENT_ID`:

**`373632460690310718`**

It is used for:

- `/oauth/v2/authorize` (`client_id` query parameter)
- `POST /oauth/v2/token` (authorization_code and refresh_token grants)

The app does **not** call `GET /backend/oauth/config`.

## Authentication model

- **Authorization Code + PKCE (S256)** via system browser
- **Android:** Chrome Custom Tabs
- **iOS:** `ASWebAuthenticationSession`
- **Token exchange:** `POST https://zitadel.pxteam.ir/oauth/v2/token`
- **Profile:** `GET https://zitadel.pxteam.ir/backend/api/whoami` with `Bearer <access_token>`

## Run on Android

Select run configuration **`androidApp`**.

```bash
./gradlew :androidApp:assembleDebug
```

## Configuration

| Setting | Location |
|---------|----------|
| Client ID | `AuthConfig.CLIENT_ID` |
| Redirect URI | `AuthConfig.REDIRECT_URI` |
| Issuer | `AuthConfig.ISSUER` |
