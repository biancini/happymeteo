'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import logging
import webapp2

from models import User
from utils import check_hash

class ChangePasswordHandler(webapp2.RequestHandler):

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
          raise Exception('La vecchia password specificata e\' sbagliata')
     
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
