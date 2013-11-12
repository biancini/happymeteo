'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import logging
import webapp2

from datetime import datetime

from models import Answer, User, IgnoredAnswer
from utils import check_hash, happymeteo

class SubmitQuestionsHandler(webapp2.RequestHandler):

  @check_hash
  def post(self):
    data = {}
    try:
        questions = self.request.get('questions')
        user_id = self.request.get('user_id')
        latitude = self.request.get('latitude')
        longitude = self.request.get('longitude')
        timestamp = self.request.get('timestamp')
        
        if not timestamp or timestamp == "":
            raise Exception('Devi specificare un timestamp')
        
        answers = Answer.gql("WHERE user_id = :1 AND timestamp = :2", user_id, timestamp)
        
        if answers.count() > 0:
            questions = json.loads(questions)
            for q in questions:
                answer = IgnoredAnswer(
                    user_id=user_id,
                    question_id=q,
                    date=datetime.now(),
                    value=questions[q],
                    timestamp=timestamp)
                
                if latitude and longitude:
                   answer.location = latitude + "," + longitude
                
                answer.put()
    
            data = { 'message': 'ok' }
        else:
            user = User.get_by_id(int(user_id))
            user.contatore_impulsi = user.contatore_impulsi + 1
            user.put()
            
            questions = json.loads(questions)
            for q in questions:
                answer = Answer(
                    user_id=user_id,
                    question_id=q,
                    date=datetime.now(),
                    value=questions[q],
                    timestamp=timestamp)
                
                if latitude and longitude:
                   answer.location = latitude + "," + longitude
                
                answer.put()
    
            data = { 'message': 'ok' }
            (today_value, yesterday_value, tomorrow_value) = happymeteo(user_id)
            data['today'] = today_value
            data['yesterday'] = yesterday_value
            data['tomorrow'] = tomorrow_value
    except Exception as e:
        logging.exception(e)
        data = {
          'error': '%s' % str(e)
        }
        
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))