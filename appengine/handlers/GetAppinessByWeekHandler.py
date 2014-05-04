'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import logging
import webapp2

from datetime import date

from models import Answer
from utils import check_hash, mkDateTime, mkLastOfMonth, \
    formatDate

class GetAppinessByWeekHandler(webapp2.RequestHandler):

  @check_hash
  def post(self): 
    data = {}
    
    try:
        user_id = self.request.get('user_id')
        
        if not user_id:
           raise Exception('Devi specificare un\'user_id')
        
        data = {}
        arrayIndex = 0
        daysCount = 0
        
        today = date.today()
        lastOfMonth = mkLastOfMonth(today)
        daysInMonth = int(lastOfMonth.strftime("%d"))
        month = today.strftime("%m")
        year = today.strftime("%Y")
        
        for day in range(daysInMonth):
            currentDayOfMonth = mkDateTime("%s-%s-%s"%(year, month, day+1))
            weekDay = int(currentDayOfMonth.strftime("%w"))
            
            if weekDay == 0 or day == daysInMonth-1: # we catch Sunday and the last day of month
                # logging.info("%s %s new_index"%(currentDayOfMonth, weekDay))
                firstDayOfWeek = mkDateTime("%s-%s-%s"%(year, month, day+1-daysCount))
                answers = Answer.gql("WHERE user_id = :1 AND date >= DATE(:2) AND date <= DATE(:3) AND question_id = \'6434359225614336\'", '%s'%user_id, formatDate(firstDayOfWeek), formatDate(currentDayOfMonth))
                
                if answers.count() > 0:
                    curValue = 0;
                    curElems = 0;
                    for answer in answers:
                        curElems = curElems+1
                        curValue = curValue+int(answer.value)
                    
                    if curElems == 0:
                        data[arrayIndex] = 0
                    else:    
                        data[arrayIndex] = curValue / curElems
                else:
                    data[arrayIndex] = 0
                    
                daysCount = 0   
                arrayIndex += 1
            else:
                # logging.info("%s %s pass"%(currentDayOfMonth, weekDay))
                daysCount += 1
    except Exception as e:
       logging.exception(e)
       data = {
         'error': '%s' % str(e)
       }
       
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
