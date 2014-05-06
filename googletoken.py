#!/usr/bin/python
# Create AppEngine application
# Enter Google APIs Console -> API Access
# Create Oauth 2.0 Service Key -> (CLIENT_ID, CLIENT_SECRET, REDIRECT_URI)
# Create Application Key -> API_KEY
 
import urllib
import urllib2
import json
import webbrowser
 
CLIENT_ID = "347057775979-bvmbcc0nemljubhuuq286mgmjros165e.apps.googleusercontent.com"
CLIENT_SECRET = "ejXaIovEvd20kz6AuD-ciPNT"
REDIRECT_URI = "https://www.example.com/oauth2callback"
API_KEY = "IzaSyAgeaTN-09uTkNofLKp2h8ImIKjRgHkVqc"
REFRESH_TOKEN = "1/px2qMfvpe7zOTPiHfV8ZsyjjITWYNTljqSNi7MJz8HU"
ACCESS_TOKEN = ""
 
isFirstTime = False

def generateInstallAppUrl(scope):
    return ('https://accounts.google.com/o/oauth2/auth?'+ \
            'access_type=offline&response_type=code&' + \
            'client_id=%s&redirect_uri=%s&' + \
            'scope=%s')%(CLIENT_ID, REDIRECT_URI, scope)
 
def firstTime():
    # in this case, the scope is https://www.googleapis.com/auth/drive
    scope = "https://www.googleapis.com/auth/drive"
 
    # Call install app url
    url = generateInstallAppUrl(scope)
    webbrowser.open_new(url)
 
    # get one shot token
    ONE_SHOT_TOKEN = raw_input('Insert token: ')
    
    # Call https://accounts.google.com/o/oauth2/token to get access_token and refresh_token
    # (Handling Response in https://developers.google.com/accounts/docs/OAuth2InstalledApp)
    data = urllib.urlencode({
          'code': ONE_SHOT_TOKEN, 
          'client_id': CLIENT_ID,
          'client_secret': CLIENT_SECRET,
          'redirect_uri': REDIRECT_URI,
          'grant_type': 'authorization_code'})
    request = urllib2.Request(
          url='https://accounts.google.com/o/oauth2/token',
          data=data)
    request_open = urllib2.urlopen(request)
    response = request_open.read()
    request_open.close()
    tokens = json.loads(response)
    return (tokens['access_token'], tokens['refresh_token'])
    
def getGoogleAccessTokenFromRefreshToken():
    data = urllib.urlencode({
      'client_id': CLIENT_ID,
      'client_secret': CLIENT_SECRET,
      'refresh_token': REFRESH_TOKEN,
      'grant_type': 'refresh_token'})
    request = urllib2.Request(
      url='https://accounts.google.com/o/oauth2/token',
      data=data)
    request_open = urllib2.urlopen(request)
    response = request_open.read()
    request_open.close()
    tokens = json.loads(response)
    return tokens['access_token']

def revokeGoogleToken(token):
    data = urllib.urlencode({
      'token': token })
    request = urllib2.Request(
      url='https://accounts.google.com/o/oauth2/revoke',
      data=data)
    request_open = urllib2.urlopen(request)
    response = request_open.read()
    request_open.close()
 
if isFirstTime:
    # Memorize in some way refresh token or just set REFRESH_TOKEN and set isFirstTime = false
    (ACCESS_TOKEN, REFRESH_TOKEN) = firstTime()
    print "refresh token: %s" % REFRESH_TOKEN
else:
    # You shouldn't generate a new access_token every time but only when you receive an http error =P
    ACCESS_TOKEN = getGoogleAccessTokenFromRefreshToken()
 
print "access token: %s" % ACCESS_TOKEN
 
# Create Google Drive Spreadsheet file
data = {
  'title': 'new document',
  'mimeType': 'application/vnd.google-apps.spreadsheet'
}
request = urllib2.Request(
      url='https://www.googleapis.com/drive/v2/files?visibility=PRIVATE&key=%s'%API_KEY,
      data=json.dumps(data))
request.add_header('Content-Type', 'application/json')
request.add_header('Authorization', 'Bearer %s' % ACCESS_TOKEN)
request_open = urllib2.urlopen(request)
response = request_open.read()
request_open.close()

#revokeGoogleToken(ACCESS_TOKEN)
