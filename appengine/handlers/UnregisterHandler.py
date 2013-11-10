'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import webapp2

from google.appengine.ext import db

from utils import check_hash

class UnregisterHandler(webapp2.RequestHandler):

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