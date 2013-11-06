import json

from handlers import BaseRequestHandler
from models import Device
from utils import check_hash

class RegisterHandler(BaseRequestHandler):
  @check_hash
  def post(self):
    registrationId = self.request.get('registrationId')
    userId = self.request.get('userId')
    query = Device.gql("WHERE user_id = :1 AND registration_id = :2", userId, registrationId)
    
    if query.count() == 0:
       device = Device(registration_id=registrationId, user_id=userId)
       device.put()
      
    data = { 'message': 'ok' }
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))