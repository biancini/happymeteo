# -*- coding: utf-8 -*-
import urllib2
import json
import datetime
import time
import hashlib
import string
import random

from datetime import date, timedelta
from google.appengine.ext import db
from google.appengine.api import mail

from secrets import GOOGLE_API_KEY, \
    CALL_SECRET_KEY, EMAIL, PASSWORD_SECRET_KEY

def check_hash(handler_method):
    def check_hash(self, *args, **kwargs):
        if not check_call(self.request):
            data = {
              'error': 'access-denied'
            }
            
            self.response.headers['Content-Type'] = 'application/json'
            self.response.out.write(json.dumps(data))
        else:
            handler_method(self, *args, **kwargs)
    return check_hash

def check_call(request):
    arguments = request.arguments()
    arguments.sort()
    query_string = ""
    first = True
    hashing = ""
    
    for a in arguments:
        if a == "hashing":
            hashing = request.get(a)
            continue
        
        if not first:
            query_string = query_string + "&"
        
        query_string = query_string + a + "=" + request.get(a)
        first = False
        
    hash = hashlib.sha1(CALL_SECRET_KEY + query_string).hexdigest()
    
    print "query_string: %s"%query_string
    print "hashing client side: %s"%hashing
    print "hashing server side: %s"%hash
    
    return (hashing == hash)

def sendNotification(registrationId, notification_id, collapse_key=None):
    print "send message to %s with %s"%(registrationId, notification_id)
    
    data = {
      'registration_ids': [registrationId]
    }
    
    if collapse_key:
       data['collapse_key'] = collapse_key
    
    data['data'] = { 'notification_id' : notification_id }
    
    req = urllib2.Request('https://android.googleapis.com/gcm/send')
    req.add_header('Content-Type', 'application/json')
    req.add_header('Authorization', 'key=%s' % GOOGLE_API_KEY)
    response = urllib2.urlopen(req, json.dumps(data))
    response_json = json.loads(response.read())
    return response_json

def happymeteo(user_id):
    today = date.today()
    tomorrow = today + timedelta(1)
    yesterday = today - timedelta(1)
    beforeyesterday = yesterday - timedelta(1)
    query_beforeyesterday = db.GqlQuery("SELECT * FROM Answer WHERE date >= DATE(:1) AND date < DATE(:2) AND question_id = \'6434359225614336\' AND user_id = :3", formatDate(beforeyesterday), formatDate(yesterday), '%s'%user_id)
    query_yesterday = db.GqlQuery("SELECT * FROM Answer WHERE date >= DATE(:1) AND date < DATE(:2) AND question_id = \'6434359225614336\' AND user_id = :3", formatDate(yesterday), formatDate(today), '%s'%user_id)
    query_today = db.GqlQuery("SELECT * FROM Answer WHERE date >= DATE(:1) AND date < DATE(:2) AND question_id = \'6434359225614336\' AND user_id = :3", formatDate(today), formatDate(tomorrow), '%s'%user_id)
    
    beforeyesterday_value = 1.0
    beforeyesterday_sum = 0.0
    if query_beforeyesterday.count() > 0:
        for answer in query_beforeyesterday:
            beforeyesterday_sum = beforeyesterday_sum + int(answer.value)
        
        beforeyesterday_value = beforeyesterday_sum / query_beforeyesterday.count()
        
    yesterday_value = 1.0
    yesterday_sum = 0.0
    if query_yesterday.count() > 0:
        for answer in query_yesterday:
            yesterday_sum = yesterday_sum + int(answer.value)
        
        yesterday_value = yesterday_sum / query_yesterday.count()
        
    today_value = 1.0
    today_sum = 0.0
    if query_today.count() > 0:
        for answer in query_today:
            today_sum = today_sum + int(answer.value)
            
        today_value = today_sum / query_today.count()
    else:
        beforebeforeyesterday = beforeyesterday - timedelta(1)
        query_beforebeforeyesterday = db.GqlQuery('SELECT * FROM Answer WHERE date >= DATE(:1) AND date < DATE(:2) AND question_id = \'6434359225614336\' AND user_id = :3', formatDate(beforebeforeyesterday), formatDate(beforeyesterday), '%s'%user_id)
        beforebeforeyesterday_value = 1.0
        beforebeforeyesterday_sum = 0.0
        if query_beforebeforeyesterday.count() > 0:
            for answer in query_today:
                beforebeforeyesterday_sum = beforebeforeyesterday_sum + int(answer.value)
        
            beforebeforeyesterday_value = beforebeforeyesterday_sum / query_beforebeforeyesterday.count()
            
        today_value = int((beforebeforeyesterday_value + yesterday_value + beforeyesterday_value) / 3)
        
    if yesterday_value < 1:
        yesterday_value = 1
        
    if today_value < 1:
        today_value = 1
        
    tomorrow_value = int((today_value + yesterday_value + beforeyesterday_value) / 3)
    
    return (int(today_value), int(yesterday_value), int(tomorrow_value))

def sample(random, population, k):
    """Chooses k unique random elements from a population sequence.

    Returns a new list containing elements from the population while
    leaving the original population unchanged.  The resulting list is
    in selection order so that all sub-slices will also be valid random
    samples.  This allows raffle winners (the sample) to be partitioned
    into grand prize and second place winners (the subslices).

    Members of the population need not be hashable or unique.  If the
    population contains repeats, then each occurrence is a possible
    selection in the sample.

    To choose a sample in a range of integers, use xrange as an argument.
    This is especially fast and space efficient for sampling from a
    large population:   sample(xrange(10000000), 60)
    """

    # Sampling without replacement entails tracking either potential
    # selections (the pool) in a list or previous selections in a set.

    # When the number of selections is small compared to the
    # population, then tracking selections is efficient, requiring
    # only a small set and an occasional reselection.  For
    # a larger number of selections, the pool tracking method is
    # preferred since the list takes less space than the
    # set and it doesn't suffer from frequent reselections.

    n = population.count()
    if not 0 <= k <= n:
        raise ValueError("sample larger than population")
    #random = self.random
    _int = int
    result = [None] * k
    setsize = 21        # size of a small set minus size of an empty list
    if n <= setsize or hasattr(population, "keys"):
        # An n-length list is smaller than a k-length set, or this is a
        # mapping type so the other algorithm wouldn't work.
        pool = list(population)
        for i in xrange(k):         # invariant:  non-selected at [0,n-i)
            j = _int(random() * (n-i))
            result[i] = pool[j]
            pool[j] = pool[n-i-1]   # move non-selected item into vacancy
    else:
        try:
            selected = set()
            selected_add = selected.add
            for i in xrange(k):
                j = _int(random() * n)
                while j in selected:
                    j = _int(random() * n)
                selected_add(j)
                result[i] = population[j]
        except (TypeError, KeyError):   # handle (at least) sets
            raise ValueError("TypeError")
    return result

def point_inside_polygon(lat, lng, coordinates):
    x = lng
    y = lat
    poly = coordinates

    n = len(poly)
    inside =False

    p1x,p1y = poly[0]
    for i in range(n+1):
        p2x,p2y = poly[i % n]
        if y > min(p1y,p2y):
            if y <= max(p1y,p2y):
                if x <= max(p1x,p2x):
                    if p1y != p2y:
                        xinters = (y-p1y)*(p2x-p1x)/(p2y-p1y)+p1x
                    if p1x == p2x or x <= xinters:
                        inside = not inside
        p1x,p1y = p2x,p2y

    return inside

def send_new_password(first_name, last_name, email, text):
    new_password = ''.join(random.choice(string.ascii_uppercase + string.digits) for x in range(6))
    message = mail.EmailMessage(sender="happymeteo <%s>" % EMAIL,
                            subject="Nuova password su Appy Meteo")
    message.to = "%s %s <%s>" % (first_name, last_name, email)
    message.body = text.encode('utf-8') % (first_name, new_password)
    message.send()
    
    hash = hashlib.sha1(PASSWORD_SECRET_KEY + new_password).hexdigest()
    return hash

def mkDateTime(dateString,strFormat="%Y-%m-%d"):
    # Expects "YYYY-MM-DD" string
    # returns a datetime object
    eSeconds = time.mktime(time.strptime(dateString,strFormat))
    return datetime.datetime.fromtimestamp(eSeconds)

def formatDate(dtDateTime,strFormat="%Y-%m-%d"):
    if not dtDateTime:
        return ""
    
    # format a datetime object as YYYY-MM-DD string and return
    return dtDateTime.strftime(strFormat)

def mkFirstOfMonth(dtDateTime):
    #what is the first day of the current month
    #format the year and month + 01 for the current datetime, then form it back
    #into a datetime object
    return mkDateTime(formatDate(dtDateTime,"%Y-%m-01"))

def mkLastOfMonth(dtDateTime):
    dYear = dtDateTime.strftime("%Y")        #get the year
    dMonth = str(int(dtDateTime.strftime("%m"))%12+1)#get next month, watch rollover
    dDay = "1"                               #first day of next month
    nextMonth = mkDateTime("%s-%s-%s"%(dYear,dMonth,dDay))#make a datetime obj for 1st of next month
    delta = datetime.timedelta(seconds=1)    #create a delta of 1 second
    return nextMonth - delta                 #subtract from nextMonth and return
