import datetime
import json
import logging

from handlers import BaseRequestHandler
from models import Answer, User
from utils import check_hash, happymeteo


class SubmitQuestionsHandler(BaseRequestHandler):

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
            raise Exception('You need to specify the timestamp')
        
        answers = Answer.gql("WHERE user_id = :1 AND timestamp = :2", user_id, timestamp)
        
        if answers.count() > 0:
            raise Exception('You already answer this impulse')
        
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