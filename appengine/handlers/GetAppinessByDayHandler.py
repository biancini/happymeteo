'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import logging
import webapp2

from datetime import date

from models import Answer
from utils import check_hash, mkFirstOfMonth, formatDate

class GetAppinessByDayHandler(webapp2.RequestHandler):

  @check_hash
  def post(self): 
    data = {}
    
    try:
        user_id = self.request.get('user_id')
        
        if not user_id:
           raise Exception('Devi specificare un user_id') 
        
        count = {}
        sum = {}
        
        firstOfMonth = mkFirstOfMonth(date.today())
        
        answers = Answer.gql("WHERE user_id = :1 AND date >= DATE(:2) AND question_id = \'6434359225614336\'", '%s'%user_id, formatDate(firstOfMonth))
        
        if answers.count() > 0:
            for answer in answers:
                index = str(answer.date.date())
                if index in count:
                    count[index] = count[index]+1
                    sum[index] = sum[index]+int(answer.value)
                else:
                    count[index] = 1
                    sum[index] = int(answer.value)
                    
            for index in count:
                data[index] = sum[index] / count[index]
    except Exception as e:
       logging.exception(e)
       data = {
         'error': '%s' % str(e)
       }
       
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))