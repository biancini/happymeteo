import json
import logging

from handlers import BaseRequestHandler
from models import User
from secrets import LOST_PASSWORD_EMAIL
from utils import check_hash, send_new_password

class LostPasswordHandler(BaseRequestHandler):

  @check_hash
  def post(self):
    try:
      email = self.request.get('email')
      
      if not email:
         raise Exception('Devi specificare una email')
     
      query = User.gql("WHERE email = :1", email)
      if query.count() == 0:
          raise Exception('Devi specificare una email utilizzata nel sistema')
      
      user = query.get()
     
      data = {'message': 'ok'}
    
      new_password = send_new_password(user.first_name, user.last_name, user.email, LOST_PASSWORD_EMAIL)
      user.password = new_password
      user.put()
    except Exception as e:
     logging.exception(e)
     data = {
       'error': '%s' % str(e)
     }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))