'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import webapp2

from models import Marker

class GetDataMapHandler(webapp2.RequestHandler):

  def get(self):
    data = []
    
    markers = Marker.all()
    for marker in markers:
        data.append({'name': marker.name, 'coordinate': str(marker.coordinate), 'appyness': marker.appyness, 'type': marker.type })
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))