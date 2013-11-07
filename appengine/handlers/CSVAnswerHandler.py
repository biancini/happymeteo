from handlers import BaseRequestHandler
from models import Answer
import csv

class CSVAnswerHandler(BaseRequestHandler):

  def get(self):
    answers = Answer.all()
    
    self.response.headers['Content-Type'] = 'application/csv'
    writer = csv.writer(self.response.out)
    writer.writerow(Answer.toHeadArray())
    
    for answer in answers:
        writer.writerow(answer.toArray())     