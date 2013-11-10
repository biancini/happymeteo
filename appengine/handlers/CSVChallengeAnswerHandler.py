'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import csv
import webapp2

from models import ChallengeAnswer

class CSVChallengeAnswerHandler(webapp2.RequestHandler):

  def get(self):
    answers = ChallengeAnswer.all()
    
    self.response.headers['Content-Type'] = 'application/csv'
    writer = csv.writer(self.response.out)
    writer.writerow(ChallengeAnswer.toHeadArray())
    
    for answer in answers:
        writer.writerow(answer.toArray())     