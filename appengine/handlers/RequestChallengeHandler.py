'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import logging
import webapp2

from google.appengine.ext import db
from datetime import datetime

from models import User, Challenge, Notification
from utils import check_hash, sendNotification

class RequestChallengeHandler(webapp2.RequestHandler):
    
  @check_hash
  def post(self):
    try:
        userId = self.request.get('userId')
        registrationId = self.request.get('registrationId')
        facebookId = self.request.get('facebookId')
        
        data = {}
        
        user_a = User.get_by_id(int(userId))
        if not user_a:
            raise Exception('Nessun utente trovato')
        
        query1 = db.GqlQuery("SELECT * FROM User WHERE facebook_id = :1", str(facebookId))
        if query1.count() == 0:
            raise Exception('Nessun utente trovato')
        
        user_b = query1.get()
        if userId == str(user_b.key().id()):
            raise Exception('Non puoi sfidare te stesso')
        
        query2 = db.GqlQuery("SELECT * FROM Device WHERE user_id = :1", str(user_b.key().id()))
        if query2.count() == 0:
            raise Exception('Nessun device trovato')
        
        # Save challenge
        query = Challenge.gql("WHERE user_id_a = :1 and user_id_b = :2 and accepted = false and turn = 0", userId, '%s' % user_b.key().id())
        add = False
        if query.count() > 0:
          challenge = query.get()
          
          if challenge:
              challenge.registration_id_a = registrationId
              challenge.created = datetime.now()
              challenge.turn = 0
              challenge.put()
          else:
              add = True
        else:
          add = True
          
        if add:
          challenge = Challenge(user_id_a=userId, user_id_b='%s'%user_b.key().id(), registration_id_a=registrationId, accepted=False, turn=0)
          challenge.put()
          
        # Send request to all devices of user_b
        for device in query2.run():
            notification = Notification(payload=db.Text(json.dumps({'user_id': challenge.user_id_b, 
                                                 'challenge_id': '%s'%challenge.key().id(), 
                                                 'adversary_facebook_id': user_a.facebook_id,
                                                 'adversary_name': user_a.first_name })))
            notification.save()
            sendNotification(device.registration_id, notification.key().id(), collapse_key='request_challenge')
        
        data = {
          'message': 'ok'
        }
    except Exception as e:
        logging.exception(e)
        data = {
          'error': '%s' % str(e)
        }
        
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))