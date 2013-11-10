'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import logging
import webapp2

from datetime import datetime

from models import Challenge, User, ChallengeQuestion, ChallengeAnswer
from utils import check_hash, sendMessage


class SubmitChallengeHandler(webapp2.RequestHandler):

  @check_hash
  def post(self):
    data = {}
    try:
        challenge_id = self.request.get('challenge_id')
        turn = self.request.get('turn')
        questions = self.request.get('questions')
        user_id = self.request.get('user_id')
        longitude = self.request.get('longitude')
        latitude = self.request.get('latitude')
        
        challenge = Challenge.get_by_id(int(challenge_id))
    
        if not challenge:
            raise Exception('Sfida non trovata')
        
        if not challenge.accepted:
            raise Exception('C\'è stato un errore con la sfida: stai rispondendo a una sfida non accettata')
        
        if int(turn) != int(challenge.turn):
            raise Exception('C\'è stato un errore con la sfida: turno sbagliato')
        
        user_a = User.get_by_id(int(challenge.user_id_a))
        if not user_a:
            raise Exception('C\'è stato un errore con la sfida: utente sfidante non esiste')
        
        user_b = User.get_by_id(int(challenge.user_id_b))
        if not user_b:
            raise Exception('C\'è stato un errore con la sfida: utente sfidato non esiste')
        
        questions = json.loads(questions)
        score = 0
        
        for q in questions:
            question = ChallengeQuestion.get_by_id(int(q))
            score = score + float(questions[q]) * question.weight
            
            challengeAnswer = ChallengeAnswer(
                user_id=user_id,
                question_id=q,
                date=datetime.now(),
                value=questions[q],
                challenge_id=challenge_id,
                turn=turn)
            
            if latitude and longitude:
               challengeAnswer.location = latitude + "," + longitude
            
            challengeAnswer.put()
            
        # calcolare lo score
        data = {'score': score}
        
        # aggiornare il challenge & Se primo turno manda la notifica a utente b o b manda la fine ad a
        if turn == "1":
            challenge.score_a = float(score)
            challenge.turn = 2
            sendMessage(challenge.registration_id_b, payload={'user_id': challenge.user_id_b, 'appy_key': 'accepted_challenge_turn2', 'score': score, 'challenge_id': challenge.key().id(), 'turn': '2'})
            data['tuFacebookId'] = user_b.facebook_id
            data['tuName'] = user_b.first_name
        else:
            challenge.score_b = float(score)
            challenge.turn = 3
            sendMessage(challenge.registration_id_a, payload={'user_id': challenge.user_id_a, 'appy_key': 'accepted_challenge_turn3', 
                                                              'ioChallenge': challenge.score_a, 'tuFacebookId': user_b.facebook_id, 
                                                              'tuName': user_b.first_name, 'tuChallenge': score, 'challenge_id': challenge.key().id(), 'turn': '3'})
            data['tuFacebookId'] = user_a.facebook_id
            data['tuName'] = user_a.first_name
            
        challenge.put()
    except Exception as e:
       logging.exception(e)
       data = {
         'error': '%s' % str(e)
       }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))