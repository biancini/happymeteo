#!/bin/sh

cd android
if env | grep ANDROID_HOME
then
	echo "Clean & Build with Gradle..."
	./gradlew clean build

	echo "Sign the jar..."
	jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore release/appymeteo.keystore happymeteo/build/apk/happymeteo-release-unsigned.apk appymeteo

	echo "Align signed jar..."
	$ANDROID_HOME/tools/zipalign -f -v 4 happymeteo/build/apk/happymeteo-release-unsigned.apk release/appymeteo.apk

	echo "Done."
else
	echo "You have to set the ANDROID_HOME environment variable."
fi
