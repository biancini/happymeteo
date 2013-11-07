import time

from google.appengine.ext import db

from handlers import BaseRequestHandler
from models import Device
from utils import sendMessage

class SendQuestionsHandler(BaseRequestHandler):

  def get(self):
    ts = time.time()
    devices = Device.all()
    for device in devices:
        if device.user_id != "":
            response_json = sendMessage(device.registration_id, collapse_key='questions', payload={'user_id': device.user_id, 'timestamp': '%s'%ts})
            if response_json['failure'] == 1:
                db.delete(device)
        else:
            db.delete(device)