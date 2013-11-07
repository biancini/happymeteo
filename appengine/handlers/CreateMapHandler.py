from datetime import date, timedelta
import json

from handlers import BaseRequestHandler
from models import Region, Provincia, Answer, Marker
from utils import formatDate, point_inside_polygon

class CreateMapHandler(BaseRequestHandler):

  def get(self):
      today = date.today()
      yesterday = today - timedelta(1)
      
      regions = Region.all()
      provincie = Provincia.all()
      answers = Answer.gql('WHERE date >= DATE(:1) AND date < DATE(:2) AND question_id = \'6434359225614336\'', formatDate(yesterday), formatDate(today))
      
      if answers.count() == 0:
          raise Exception('No answers') 
      
      data = {}
      
      for region in regions:
        id = str(region.key().id())
        data[id] = {}
        data[id]['name'] = region.name
        data[id]['sum'] = 0
        data[id]['count'] = 0
        data[id]['type'] = 'R'
        data[id]['coordinate'] = region.coordinate
        
      for provincia in provincie:
        id = str(provincia.key().id())
        data[id] = {}
        data[id]['name'] = provincia.name
        data[id]['sum'] = 0
        data[id]['count'] = 0
        data[id]['type'] = 'P'
        data[id]['coordinate'] = provincia.coordinate
      
      for answer in answers:
          if answer.location:
            lat = answer.location.lat
            lng = answer.location.lon
            
            for region in regions:
                id = str(region.key().id())
                geometry = json.loads(region.geometry)
                
                is_inside = False
                
                if 'geometries' in geometry:
                    is_inside = False
                    for g in geometry['geometries']:
                        if point_inside_polygon(lat, lng, g['coordinates'][0]):
                            is_inside = True
                            break
                    
                if 'geometry' in geometry:
                    is_inside = point_inside_polygon(lat, lng, geometry['geometry']['coordinates'][0])
                    
                if is_inside:
                    data[id]['sum'] = data[id]['sum'] + int(answer.value)
                    data[id]['count'] = data[id]['count'] + 1
            
            for provincia in provincie:
                id = str(provincia.key().id())
                geometry = json.loads(provincia.geometry)
                
                is_inside = False
                
                if 'geometries' in geometry:
                    is_inside = False
                    for g in geometry['geometries']:
                        if point_inside_polygon(lat, lng, g['coordinates'][0]):
                            is_inside = True
                            break
                    
                if 'geometry' in geometry:
                    is_inside = point_inside_polygon(lat, lng, geometry['geometry']['coordinates'][0])
                    
                if is_inside:
                    data[id]['sum'] = data[id]['sum'] + int(answer.value)
                    data[id]['count'] = data[id]['count'] + 1
    
      for id, object in data.iteritems():
          if object['count'] == 0:
              appyness = 1
          else:
              appyness = object['sum']/object['count']
          marker = Marker.gql('WHERE id = :1', id)
          marker = marker.get()
          
          if marker:
              marker.coordinate=object['coordinate']
              marker.appyness=appyness
              marker.name=object['name']
              marker.type=object['type']
              marker.put()
          else:
              marker = Marker(id=id, coordinate=object['coordinate'], appyness=appyness, name=object['name'], type=object['type'])
              marker.put()
      
      self.response.headers['Content-Type'] = 'application/json'
      self.response.out.write("ok")