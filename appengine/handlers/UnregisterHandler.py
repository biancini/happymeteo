import json

from google.appengine.ext import db

from handlers import BaseRequestHandler
from utils import check_hash

class UnregisterHandler(BaseRequestHandler):

  @check_hash
  def post(self):
    registrationId = self.request.get('registrationId')
    query = db.GqlQuery("SELECT * FROM Device WHERE registration_id = :1", registrationId)
    for device in query.run(limit=1):
      print "delete %s" % (device.registration_id)
      db.delete(device)
    
    data = { 'message': 'ok' }
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))