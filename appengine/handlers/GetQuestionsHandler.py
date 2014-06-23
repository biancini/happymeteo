'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import webapp2

from random import random

from models import Question, ChallengeQuestion
from utils import check_hash, sample


class GetQuestionsHandler(webapp2.RequestHandler):

  @check_hash
  def post(self):
    app_version = self.request.get('version', None)

    if not app_version:
       questions = Question.gql("Order BY order")
    
       if questions.count() > 0:
          self.response.headers['Content-Type'] = 'application/json'
          self.response.out.write(json.dumps([q.toJson() for q in questions]))
    else:
       q = Question.gql("Order BY order")
       questions = []

       if q.count() > 0:
          data = [q.get().toJson()]
          questions.append(data[0])

       q = ChallengeQuestion.gql("Order BY order")
       if q.count() > 0:
          data = sample(random, q, 3)
          questions.extend([q.toJson() for q in data])

       self.response.headers['Content-Type'] = 'application/json'
       self.response.out.write(json.dumps(questions))
