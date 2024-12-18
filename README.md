# forzautils_android
Android application for monitoring Forza telemetry data.
This app requires the `motorsport` branch of [ForzaTelemetryAPI](https://https://github.com/dusanders/ForzaTelemetryAPI)
Please clone the repository beside this project so Gradle will build - or, adjust the gradle to point to the proper directory.
```gradle
android {
    .
    .
    .
    sourceSets {
        getByName("main") {
            java {
                srcDir("../../ForzaTelemetryApi/src")
            }
        }
    }
}
```