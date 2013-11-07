from handlers import BaseRequestHandler
from models import User
import csv

class CSVUserHandler(BaseRequestHandler):

  def get(self):
    users = User.all()
    
    self.response.headers['Content-Type'] = 'application/csv'
    writer = csv.writer(self.response.out)
    writer.writerow(User.toHeadArray())
    
    for user in users:
        writer.writerow(user.toArray())     