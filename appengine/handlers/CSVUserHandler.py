'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import csv
import webapp2

from models import User

class CSVUserHandler(webapp2.RequestHandler):

  def get(self):
    users = User.all()
    
    self.response.headers['Content-Type'] = 'application/csv'
    writer = csv.writer(self.response.out)
    writer.writerow(User.toHeadArray())
    
    for user in users:
        writer.writerow(user.toArray())     