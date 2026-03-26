# SecureFireTV

Android TV (Leanback) app that fetches a catalog from a backend JSON API and plays streams through Media3 ExoPlayer.

## Backend configuration
The catalog endpoint is configured via a string resource:

```
app/src/main/res/values/strings.xml
<string name="backend_url">https://jsonplaceholder.typicode.com/albums/1/photos</string>
```

Update this value to point at your own catalog service. The expected JSON shape per item is:

```json
{
  "id": 1,
  "title": "Example Title",
  "url": "https://example.com/poster.jpg"
}
```

## Release signing (optional)
To enable release signing, add a `keystore.properties` file at the repo root with:

```
storeFile=path/to/your.keystore
storePassword=yourStorePassword
keyAlias=yourAlias
keyPassword=yourKeyPassword
```

If the file is missing, the release build will skip signing configuration.

## Build
```
./gradlew assembleDebug
```
