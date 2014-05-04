'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import logging
import urllib2
import webapp2

from datetime import datetime
from google.appengine.ext import db

from utils import check_hash, happymeteo

class FacebookLoginHandler(webapp2.RequestHandler):
  def calculate_age(self, born):
    today = datetime.date.today()
    try:
      birthday = born.replace(year=today.year)
    except ValueError:  # raised when birth date is February 29 and the current year is not a leap year
      birthday = born.replace(year=today.year, day=born.day - 1)
    if birthday > today:
      return today.year - born.year - 1
    else:
      return today.year - born.year

  # get_age: birthday in mm/gg/yyyy
  # 0 => 0 -24 years
  # 1 => 25 - 35 years
  # 2 => 35 - 50 years
  # 3 => > 50 years 
  def get_age(self, born):
    try:
      age = self.calculate_age(datetime.strptime(born, "%m/%d/%Y"))

      if age < 25:
        return 0

      if age < 36:
        return 1

      if age < 51:
        return 2

      return 3
    except:
      return 0

  @check_hash
  def post(self):
    data = {}
    
    try:
      accessToken = self.request.get('accessToken')
      facebook_profile = json.load(urllib2.urlopen("https://graph.facebook.com/me?access_token=%s" % accessToken))
 
      query = db.GqlQuery("SELECT * FROM User WHERE facebook_id = :1",
          facebook_profile['id'])

      if query.count() > 0:
        user = query.get()
        data = user.toJson()
        (today_value, yesterday_value, tomorrow_value) = happymeteo(data['user_id'])
        data['today'] = today_value
        data['yesterday'] = yesterday_value
        data['tomorrow'] = tomorrow_value
      else:
        if 'email' in facebook_profile:
            email = facebook_profile['email']
        else:
            email = ''


        data = {
            'user_id': '',
            'facebook_id': facebook_profile['id'],
            'first_name': facebook_profile['first_name'],
            'last_name': facebook_profile['last_name'],
            'email': email,
            'age': self.get_age(facebook_profile['birthday']),
            'education': '0',
            'cap': '',
            'work': '0',
            'registered': '0'
        }
        
        if facebook_profile['gender'] == "male":
          data['gender'] = 1
        else:
          data['gender'] = 0

    except Exception as e:
      logging.exception(e)
      data = {
        'error': '%s' % str(e)
      }
      
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
