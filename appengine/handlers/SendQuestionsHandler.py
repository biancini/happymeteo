'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import time
import webapp2
import logging

from google.appengine.ext import db

from models import Device, Notification
from utils import sendNotification
import json

class SendQuestionsHandler(webapp2.RequestHandler):

  def get(self):
    try:
        user_id = self.request.get('user_id')
        
        user_notification = {}
        
        if not user_id:
            devices = Device.all()
        else:
            devices = Device.gql("WHERE user_id = :1", user_id)
            
        ts = time.time()
        for device in devices:
            if device.user_id != "":
                
                notification_id = None
                
                if device.user_id in user_notification:
                    notification_id = user_notification[device.user_id]
                else:
                    notification = Notification(payload=db.Text(json.dumps({'user_id': device.user_id, 'timestamp': '%s'%ts, 'collapse_key': 'questions'})))
                    notification.save()
                    notification_id = notification.key().id()
                    user_notification[device.user_id] = notification_id
                    
                if notification_id:
                    response_json = sendNotification(device.registration_id, notification_id, collapse_key='questions')
                    if response_json['failure'] == 1:
                        db.delete(device)
            else:
                db.delete(device)
    except Exception as e:
        logging.exception(e)