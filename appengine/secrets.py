# -*- coding: utf-8 -*-
# Copy this file into secrets.py and set keys, secrets and scopes.

CUR_VERSION = 'Versione 0.3.4'

# This is a session secret key used by webapp2 framework.
# Get 'a random and long string' from here: 
# http://clsc.net/tools/random-string-generator.php
# or execute this from a python shell: import os; os.urandom(64)
SESSION_KEY = "8\xa6\x1e\x11,\x93I\x1aP\xf9\\\xa0\xe1\xde\x8e+\x1dn\xd0\xe8\x1e\x92\xfa\x93\xed\xbf\xcd\xe1\x1f\xa1\x8b\xdf\xc6\xa0\xa73z\xe9\xf8\xb3\xee\x97\xc0\xba\xea\x9cOzXy\xe7\x1ca\x03j\xa7An\x94\x8e\x12"

# Google API KEY for Happymeteo
GOOGLE_API_KEY = 'AIzaSyBfRX3EH1n7KKx53WyASzjLDRD3qOwnl5k'

# Password secret key
PASSWORD_SECRET_KEY = 'f01a1a0bd409957b9305d2dc21c6b066859f51447c04adfb0b21c9fb5cff9eb0a6573c69e62fcb97321d878bfecb7d2cc8e6650ac6db1a1ef3125373d3e81b85'

# Call secret key
CALL_SECRET_KEY ='be07735dae6fdd28cd11b8a1840db9ecf7db2f1e9001747cac85c8fd86a14732e56ba23588d234c5794805f13fc4776cd2238f375f136fdf8bd9d86c5b0f5938'

# Email and password appymeteo account
EMAIL = 'appymeteo@gmail.com'
PASSWORD = 'vivaappymeteo!'

# fusion tables
DOMANDA = "18GPlkUTN9Qbi_JaZVkAvFKWyiQNgO-OD8v8M5W8"
SFIDA = "1dRqKaRI4lCRT6TRnbqloYM4U7m2UK3d7oVM4W0o"
RISPOSTA = "1JMpJQMAWxqF3Kd18TFgqvyCBZUBH8N1Vx6YAZ3Y"
RISPOSTA_SFIDA = "1xlr5InohkX_CElhTaMU4PZeRhfjBQs--Pe54d2s"

CREATE_ACCOUNT_EMAIL = """Benvenuto %s,\n
Il tuo account su appymeteo ha bisogno di essere verificato, per farlo clicca sul link sottostante:
https://happymeteo.appspot.com/confirm_user?confirmation_code=%s\n
\n
Saluti,\n
Appymeteo"""

CHANGE_FACEBOOK_EMAIL = """Ciao %s,\n
Il tuo account su appymeteo non &egrave; pi&ugrave; collegato a Facebook, per
accedere al tuo profilo da ora in poi utilizza la tua email
e la seguente password: %s<br&>
<br&>
Saluti,\n
Appymeteo"""

LOST_PASSWORD_EMAIL = """Ciao %s,\n
Per accedere al tuo profilo utilizza la tua email e la seguente password: %s\n
\n
Saluti,\n
Appymeteo"""
