'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import webapp2
import logging

from models import MapMarker

class GetDataMapHandler(webapp2.RequestHandler):

  def getProvince(self):
    province_m = MapMarker.gql('WHERE type = \'Province\' ORDER BY date DESC LIMIT 1')
    for provincia in province_m:
        return json.loads(provincia.valore)

  def getRegioni(self):
    regioni_m = MapMarker.gql('WHERE type = \'Regioni\' ORDER BY date DESC LIMIT 1')
    for regione in regioni_m:
        return json.loads(regione.valore)

  def get(self):
    data = []
    province = self.getProvince()
    regioni = self.getRegioni()

    #markers = Marker.all()
    #for marker in markers:
    #    data.append({'name': marker.name, 'coordinate': str(marker.coordinate), 'appyness': marker.appyness, 'type': marker.type })

    for key, provincia in province.items():
        data.append({
            'name': provincia['name'],
            'coordinate': '%s,%s' % (provincia['lat'], provincia['lon']),
            'appyness': provincia['appyness'],
            'type': 'P' })
        
    for key, regione in regioni.items():
        data.append({
            'name': regione['name'],
            'coordinate': '%s,%s' % (regione['lat'], regione['lon']),
            'appyness': regione['appyness'],
            'type': 'R' })
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
