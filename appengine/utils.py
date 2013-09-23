import urllib
import urllib2
import json

from secrets import GOOGLE_API_KEY, CLIENT_ID, CLIENT_SECRET, REFRESH_TOKEN

def sendMessage(registrationId, payload):
    print "send message to %s"%registrationId
    
    data = {
      'registration_ids': [registrationId]
    }
    
    if payload:
       data['data'] = payload 
    
    req = urllib2.Request('https://android.googleapis.com/gcm/send')
    req.add_header('Content-Type', 'application/json')
    req.add_header('Authorization', 'key=%s' % GOOGLE_API_KEY)
    response = urllib2.urlopen(req, json.dumps(data))
    print response.read()

def sendSyncMessage(registrationId, collapse_key, payload=None):
    print "send message to %s"%registrationId
    
    data = {
      'registration_ids': [registrationId],
      'collapse_key': collapse_key
    }
    
    if payload:
       data['data'] = payload 
    
    req = urllib2.Request('https://android.googleapis.com/gcm/send')
    req.add_header('Content-Type', 'application/json')
    req.add_header('Authorization', 'key=%s' % GOOGLE_API_KEY)
    response = urllib2.urlopen(req, json.dumps(data))
    print response.read()
    
def getGoogleAccessToken():
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
    
def sqlGetFusionTable(access_token, sql):
    request = urllib2.Request(url='https://www.googleapis.com/fusiontables/v1/query?%s' % \
                              (urllib.urlencode({'access_token': access_token,
                               'sql': sql})))
    request_open = urllib2.urlopen(request)
    response = request_open.read()
    request_open.close()
    return response

def sqlPostFusionTable(access_token, sql):
    print access_token + " " + sql
    data = urllib.urlencode({'sql': sql})
    request = urllib2.Request(url='https://www.googleapis.com/fusiontables/v1/query',
                              data=data)
    request.add_header('Authorization', 'Bearer %s' % access_token)
    request_open = urllib2.urlopen(request)
    response = request_open.read()
    request_open.close()
    return response