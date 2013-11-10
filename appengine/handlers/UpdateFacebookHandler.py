'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import logging
import webapp2

from models import User
from utils import check_hash

class UpdateFacebookHandler(webapp2.RequestHandler):

  @check_hash
  def post(self):
    try:
      user_id = self.request.get('user_id')
      facebook_id = self.request.get('facebook_id')
      
      if not user_id:
         raise Exception('Devi specificare un user_id')
     
      # if not facebook_id:
      #   raise Exception('Devi specificare un facebook_id')
     
      user = User.get_by_id(int(user_id))
      if not user:
          raise Exception('Devi specificare un utente valido')
      
      data = {'message': 'ok', 'facebook_id': facebook_id}
    
      user.facebook_id = facebook_id
      user.put()
    except Exception as e:
     logging.exception(e)
     data = {
       'error': '%s' % str(e)
     }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))