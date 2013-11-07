from handlers import BaseRequestHandler
from models import ChallengeAnswer
import csv

class CSVChallengeAnswerHandler(BaseRequestHandler):

  def get(self):
    answers = ChallengeAnswer.all()
    
    self.response.headers['Content-Type'] = 'application/csv'
    writer = csv.writer(self.response.out)
    writer.writerow(ChallengeAnswer.toHeadArray())
    
    for answer in answers:
        writer.writerow(answer.toArray())     