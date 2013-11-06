import json

from handlers import BaseRequestHandler
from models import Marker

class GetDataMapHandler(BaseRequestHandler):

  def get(self):
    data = []
    
    markers = Marker.all()
    for marker in markers:
        data.append({'name': marker.name, 'coordinate': str(marker.coordinate), 'appyness': marker.appyness, 'type': marker.type })
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))