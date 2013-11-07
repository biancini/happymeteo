import json
import logging

from google.appengine.ext import db
from datetime import datetime

from handlers import BaseRequestHandler
from models import Challenge, User
from utils import check_hash, sendMessage


class AcceptChallengeHandler(BaseRequestHandler):
    
  @check_hash
  def post(self):
    try:
        challengeId = self.request.get('challengeId')
        accepted = self.request.get('accepted')
        registrationId = self.request.get('registrationId')
        userId = self.request.get('userId')
        
        data = {}
        
        challenge = Challenge.get_by_id(int(challengeId))
    
        if not challenge:
            raise Exception('Nessuna sfida trovata')
        
        if challenge.user_id_b != userId:
            raise Exception('C\'è stato un errore con la sfida')
        
        if challenge.turn > 0:
            raise Exception('Sfida scaduta')
        
        query2 = db.GqlQuery("SELECT * FROM Device WHERE registration_id = :1 and user_id = :2", registrationId, userId)
            
        if query2.count() == 0:
            raise Exception('Nessun device trovato')
        
        user_a = User.get_by_id(int(challenge.user_id_a))
        user_a.contatore_sfidante = user_a.contatore_sfidante + 1
        user_a.put()
        
        user_b = User.get_by_id(int(challenge.user_id_b))
        user_b.contatore_sfidato = user_b.contatore_sfidato + 1
        user_b.put()
        
        challenge.accepted = (accepted == "true")
        challenge.registration_id_b = registrationId
        challenge.turn = 1
        challenge.created = datetime.now()
        challenge.put()
        
        sendMessage(challenge.registration_id_a, payload={'user_id': challenge.user_id_a, 'appy_key': 'accepted_challenge_turn1_%s' % accepted, 'challenge_id': '%s'%challenge.key().id(), 'turn': '1'})
        
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