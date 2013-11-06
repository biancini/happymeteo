import json
import logging

from handlers import BaseRequestHandler
from models import User
from utils import check_hash

class ChangePasswordHandler(BaseRequestHandler):

  @check_hash
  def post(self):
    try:
      user_id = self.request.get('user_id')
      old_password = self.request.get('old_password')
      new_password = self.request.get('new_password')
      
      if not user_id:
         raise Exception('Devi specificare un user_id')
     
      user = User.get_by_id(int(user_id))
      if not user:
          raise Exception('Devi specificare un utente valido')
      
      if user.password != old_password:
          raise Exception('La vecchia password specificata è sbagliata')
     
      data = {'message': 'ok'}
    
      user.password = new_password
      user.put()
    except Exception as e:
     logging.exception(e)
     data = {
       'error': '%s' % str(e)
     }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))