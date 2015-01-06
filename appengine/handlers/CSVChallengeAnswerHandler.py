'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import csv
import webapp2

from models import ChallengeAnswer

class CSVChallengeAnswerHandler(webapp2.RequestHandler):

  def get(self):
    date_from = self.request.get("from", None)
    date_to = self.request.get("to", None)

    if date_from is None and date_to is None:
      date_from = datetime.datetime.now() - datetime.timedelta(days=2)
      date_to = datetime.datetime.now() - datetime.timedelta(days=1)
    elif date_from is None:
      date_from = date_to - datetime.timedelta(days=1)
    elif date_to is None:
      date_to = date_from + datetime.timedelta(days=1)

    answers = ChallengeAnswer.gql("WHERE date >= DATE('%s') and date <= DATE('%s')" % 
                         (date_from.strftime("%Y-%m-%d"), date_to.strftime("%Y-%m-%d")))
    
    self.response.headers['Content-Type'] = 'application/csv'
    writer = csv.writer(self.response.out)
    writer.writerow(ChallengeAnswer.toHeadArray())
    
    for answer in answers:
        writer.writerow(answer.toArray())     
