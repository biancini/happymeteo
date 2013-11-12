'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import time
import webapp2
import logging

from google.appengine.ext import db

from models import Device
from utils import sendNotification

class SendQuestionsHandler(webapp2.RequestHandler):

  def get(self):
    try:
        user_id = self.request.get('user_id')
        
        if not user_id:
            devices = Device.all()
        else:
            devices = Device.gql("WHERE user_id = :1", user_id)
            
        ts = time.time()
        for device in devices:
            if device.user_id != "":
		response_json = sendNotification(device.registration_id, {'user_id': device.user_id, 'timestamp': '%s'%ts, 'collapse_key': 'questions'}, collapse_key='questions')
		if response_json['failure'] == 1:
		   db.delete(device)
            else:
                db.delete(device)
    except Exception as e:
        logging.exception(e)
