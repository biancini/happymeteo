'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import webapp2
import logging

from datetime import date, timedelta

from models import Region, Provincia, Answer, MapMarker
from utils import formatDate, point_inside_polygon

class CreateMapHandler(webapp2.RequestHandler):

  def getRegioni(self):
      regioni = []
      regioni_m = Region.all()

      for regione in regioni_m:
          regione_new = {}
          regione_new['id'] = str(regione.key().id())
          regione_new['name'] = regione.name
          regione_new['lat'] = regione.coordinate.lat if regione.coordinate else None
          regione_new['lon'] = regione.coordinate.lon if regione.coordinate else None
          regione_new['geometry'] = regione.geometry
          regioni.append(regione_new)

      return regioni

  def getProvince(self):
      province = []
      province_m = Provincia.all()

      for provincia in province_m:
          provincia_new = {}
          provincia_new['id'] = str(provincia.key().id())
          provincia_new['name'] = provincia.name
          provincia_new['lat'] = provincia.coordinate.lat if provincia.coordinate else None
          provincia_new['lon'] = provincia.coordinate.lon if provincia.coordinate else None
          provincia_new['geometry'] = provincia.geometry
          province.append(provincia_new)

      return province

  def getAnswers(self, yesterday, today):
      answers = []
      answers_m = Answer.gql('WHERE date >= DATE(:1) AND date < DATE(:2) AND question_id = \'6434359225614336\'', formatDate(yesterday), formatDate(today))

      for answer in answers_m:
          answer_new = {}
          answer_new['lat'] = answer.location.lat if answer.location else None
          answer_new['lon'] = answer.location.lon if answer.location else None
          answer_new['value'] = answer.value
          answers.append(answer_new)

      return answers

  def get(self):
      today = date.today()
      yesterday = today - timedelta(1)
      
      regioni = self.getRegioni()
      province = self.getProvince()
      answers = self.getAnswers(yesterday, today)

      logging.info("Got all data from models.")
      
      if len(answers) == 0:
          raise Exception('No answers') 
      
      dataR = {}
      dataP = {}
      
      for regione in regioni:
        id = regione['id']
        dataR[id] = {}
        dataR[id]['name'] = regione['name']
        dataR[id]['sum'] = 0
        dataR[id]['count'] = 0
        dataR[id]['lat'] = regione['lat']
        dataR[id]['lon'] = regione['lon']
        
      for provincia in province:
        id = provincia['id']
        dataP[id] = {}
        dataP[id]['name'] = provincia['name']
        dataP[id]['sum'] = 0
        dataP[id]['count'] = 0
        dataP[id]['lat'] = provincia['lat']
        dataP[id]['lon'] = provincia['lon']
      
      for answer in answers:
          if answer['lat'] and answer['lon']:
            lat = answer['lat']
            lng = answer['lon']
            
            for regione in regioni:
                id = regione['id']
                geometry = json.loads(regione['geometry'])
                
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
                    dataR[id]['sum'] = dataR[id]['sum'] + int(answer['value'])
                    dataR[id]['count'] = dataR[id]['count'] + 1
            
            for provincia in province:
                id = provincia['id']
                geometry = json.loads(provincia['geometry'])
                
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
                    dataP[id]['sum'] = dataP[id]['sum'] + int(answer['value'])
                    dataP[id]['count'] = dataP[id]['count'] + 1
    
      for id, object in dataR.iteritems():
          if object['count'] == 0:
              dataR[id]['appyness'] = 1
          else:
              dataR[id]['appyness'] = object['sum']/object['count']

      for id, object in dataP.iteritems():
          if object['count'] == 0:
              dataP[id]['appyness'] = 1
          else:
              dataP[id]['appyness'] = object['sum']/object['count']

      logging.info("Computed all new appyness scores.")

      markerR = MapMarker(date=yesterday, type='Regioni', valore=json.dumps(dataR))
      markerR.put()

      markerP = MapMarker(date=yesterday, type='Province', valore=json.dumps(dataP))
      markerP.put()

      logging.info("Inserted new map markers in the models.")
      
      self.response.headers['Content-Type'] = 'application/json'
      self.response.out.write("ok")
