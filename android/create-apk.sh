#!/bin/sh

echo "Clean & Build with Gradle..."
./gradlew clean build

echo "Sign the jar..."
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore release/appymeteo.keystore happymeteo/build/apk/happymeteo-release-unsigned.apk appymeteo

cp happymeteo/build/apk/happymeteo-release-unsigned.apk release/appymeteo.apk 
echo "Done."
