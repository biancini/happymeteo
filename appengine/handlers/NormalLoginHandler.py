import json
import logging

from google.appengine.ext import db

from handlers import BaseRequestHandler
from utils import check_hash, happymeteo

class NormalLoginHandler(BaseRequestHandler):
  @check_hash
  def post(self):
      data = {}
      
      try:
        email = self.request.get('email')
        pwd_parameter = self.request.get('password')
        q = db.GqlQuery("SELECT * FROM User WHERE email = :1 and status = 2", email)

        if q.count() > 0:
          user = q.get()
          
          if user.password == pwd_parameter:
            data = user.toJson()
            
            (today_value, yesterday_value, tomorrow_value) = happymeteo(data['user_id'])
            data['today'] = today_value
            data['yesterday'] = yesterday_value
            data['tomorrow'] = tomorrow_value
          else:
            data = {
              'error': 'Password didn\'t match'
            }
        else:
          data = {
            'error': 'User not found or not confirmed'
          }
      except Exception as e:
        logging.exception(e)
        data = {
          'error': '%s' % str(e)
        }
      
      self.response.headers['Content-Type'] = 'application/json'
      self.response.out.write(json.dumps(data))