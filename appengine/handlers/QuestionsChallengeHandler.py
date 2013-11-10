'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import logging
import webapp2

from random import sample, random

from models import Challenge, ChallengeQuestion, ChallengeQuestionCategory, \
    ChallengeAnswer
from utils import check_hash

class QuestionsChallengeHandler(webapp2.RequestHandler):

  @check_hash
  def post(self):
    try:
        challengeId = self.request.get('challengeId')
        turn = self.request.get('turn')
        
        challenge = Challenge.get_by_id(int(challengeId))
        
        if not challenge:
            raise Exception('Nessuna sfida trovata')
        
        if challenge.turn != int(turn):
            raise Exception('C\'è stato un errore con la sfida')
        
        if turn == "1":
            questions = ChallengeQuestion.gql("WHERE category_id = 0")
            data = [questions.get().toJson()]
            
            categories = ChallengeQuestionCategory.all()
            for c in categories:
                questions = ChallengeQuestion.gql("WHERE category_id = :1", c.key().id())
                question = sample(random.random, questions, 1)
                data.append(question[0].toJson())
        else:
            answers = ChallengeAnswer.gql("WHERE challenge_id = :1", challengeId)
            questions = [ChallengeQuestion.get_by_id(int(answer.question_id)) for answer in answers]
            questions.sort(key=lambda x: x.order, reverse=False)
            data = [q.toJson() for q in questions]
    except Exception as e:
        logging.exception(e)
        data = {
          'error': '%s' % str(e)
        }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))