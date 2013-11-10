'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import csv
import webapp2

from models import Answer

class CSVAnswerHandler(webapp2.RequestHandler):

  def get(self):
    answers = Answer.all()
    
    self.response.headers['Content-Type'] = 'application/csv'
    writer = csv.writer(self.response.out)
    writer.writerow(Answer.toHeadArray())
    
    for answer in answers:
        writer.writerow(answer.toArray())     