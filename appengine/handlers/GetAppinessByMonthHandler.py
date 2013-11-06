import json
import logging

from datetime import date

from handlers import BaseRequestHandler
from models import Answer
from utils import check_hash, mkFirstOfMonth, mkDateTime, mkLastOfMonth, \
    formatDate

class GetAppinessByMonthHandler(BaseRequestHandler):

  @check_hash
  def post(self): 
    data = {}
    
    try:
        user_id = self.request.get('user_id')
        
        if not user_id:
           raise Exception('Devi specificare un user_id') 
        
        count = {}
        sum = {}
        
        dtDateTime = date.today()
        dtDateTime = mkFirstOfMonth(dtDateTime)
        month = int(dtDateTime.strftime("%m"))
        dYear = dtDateTime.strftime("%Y")
        dDay = "1"  
        len_months = 5;
        
        for i in range(len_months):
            index = str(month-i-1)
            dMonth = str((month-i-1)%12)
            firstOfMonth = mkDateTime("%s-%s-%s"%(dYear,dMonth,dDay))
            lastOfMonth = mkLastOfMonth(firstOfMonth)
            
            answers = Answer.gql("WHERE user_id = :1 AND date >= DATE(:2) AND date <= DATE(:3) AND question_id = \'6434359225614336\'", '%s'%user_id, formatDate(firstOfMonth), formatDate(lastOfMonth))
            
            if answers.count() > 0:
                count[index] = 0
                sum[index] = 0
                for answer in answers:
                    count[index] = count[index]+1
                    sum[index] = sum[index]+int(answer.value)
                if sum[index] == 0:
                    data[index] = 1
                else:    
                    data[index] = sum[index] / count[index]
    except Exception as e:
       logging.exception(e)
       data = {
         'error': '%s' % str(e)
       }
       
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))