import urllib2
import json

from secrets import GOOGLE_API_KEY

def sendToSyncMessage(registrationId, collapse_key, payload=None):
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