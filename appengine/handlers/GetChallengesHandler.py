import json
import logging

from handlers import BaseRequestHandler
from models import Challenge, User
from utils import check_hash


class GetChallengesHandler(BaseRequestHandler):

  @check_hash
  def post(self):
    try:
        user_id = self.request.get('user_id')
        
        if not user_id:
           raise Exception('You need to specify the user_id') 
       
        data = []
        
        challenges = Challenge.gql("WHERE user_id_a = :1 ORDER BY created DESC", user_id)
        if challenges.count() > 0:
            for challenge in challenges:
                user_adversary = User.get_by_id(int(challenge.user_id_b))
                if user_adversary:
                    c_object = challenge.toJson()
                    c_object['adversary'] = user_adversary.toJson()
                    data.append(c_object)
        
        challenges = Challenge.gql("WHERE user_id_b = :1 ORDER BY created DESC", user_id)
        if challenges.count() > 0:        
            for challenge in challenges:
                if challenge.turn == 0:
                    continue
                user_adversary = User.get_by_id(int(challenge.user_id_a))
                if user_adversary:
                    c_object = challenge.toJson()
                    c_object['adversary'] = user_adversary.toJson()
                    data.append(c_object)
    except Exception as e:
        logging.exception(e)
        data = {
          'error': '%s' % str(e)
        }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))