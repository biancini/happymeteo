import json

from handlers import BaseRequestHandler
from models import Question
from utils import check_hash


class GetQuestionsHandler(BaseRequestHandler):

  @check_hash
  def post(self):
    questions = Question.gql("Order BY order")
    
    if questions.count() > 0:
       self.response.headers['Content-Type'] = 'application/json'
       self.response.out.write(json.dumps([q.toJson() for q in questions]))