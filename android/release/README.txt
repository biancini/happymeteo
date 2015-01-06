Se si vuole lavorare a appy meteo e testare in debug bisogna fare:

keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64

con password android e salvare in Key Hashes in https://developers.facebook.com/apps/405414319576008/summary?ref=nav

Altrimenti se si vuole effettuare una release fare File > Export > Android Application, scegliere appymeteo.keystore e password!
