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

def happymeteo(user_id):
    from datetime import date, timedelta
    from google.appengine.ext import db
    
    today = date.today()
    tomorrow = today + timedelta(1)
    yesterday = today - timedelta(1)
    beforeyesterday = yesterday - timedelta(1)
    query_today = db.GqlQuery('SELECT * FROM Answer WHERE date >= DATE(\'%s\') AND date < DATE(\'%s\') AND question_id = \'6434359225614336\' AND user_id = \'%s\'' % (today, tomorrow, user_id))
    query_yesterday = db.GqlQuery('SELECT * FROM Answer WHERE date >= DATE(\'%s\') AND date < DATE(\'%s\') AND question_id = \'6434359225614336\' AND user_id = \'%s\'' % (yesterday, today, user_id))
    query_beforeyesterday = db.GqlQuery('SELECT * FROM Answer WHERE date >= DATE(\'%s\') AND date < DATE(\'%s\') AND question_id = \'6434359225614336\' AND user_id = \'%s\'' % (beforeyesterday, yesterday, user_id))
    
    today_value = 1.0
    today_sum = 0.0
    if query_today.count() > 0:
        for answer in query_today:
            today_sum = today_sum + int(answer.value)
        
        today_value = today_sum / query_today.count()
        
    yesterday_value = 1.0
    yesterday_sum = 0.0
    if query_yesterday.count() > 0:
        for answer in query_yesterday:
            yesterday_sum = yesterday_sum + int(answer.value)
        
        yesterday_value = yesterday_sum / query_yesterday.count()
        
    beforeyesterday_value = 1.0
    beforeyesterday_sum = 0.0
    if query_beforeyesterday.count() > 0:
        for answer in query_beforeyesterday:
            beforeyesterday_sum = beforeyesterday_sum + int(answer.value)
        
        beforeyesterday_value = beforeyesterday_sum / query_beforeyesterday.count()
        
    tomorrow_value = int((today_value + yesterday_value + beforeyesterday_value) / 3)
    
    return (int(today_value), int(yesterday_value), int(tomorrow_value))