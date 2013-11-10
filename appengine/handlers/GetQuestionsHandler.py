'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import webapp2

from models import Question
from utils import check_hash


class GetQuestionsHandler(webapp2.RequestHandler):

  @check_hash
  def post(self):
    questions = Question.gql("Order BY order")
    
    if questions.count() > 0:
       self.response.headers['Content-Type'] = 'application/json'
       self.response.out.write(json.dumps([q.toJson() for q in questions]))