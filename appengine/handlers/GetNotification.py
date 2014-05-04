'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import webapp2
import logging

from models import Notification
from utils import check_hash
import json

class GetNotification(webapp2.RequestHandler):

  @check_hash
  def post(self):
    try:
        notification_id = self.request.get('notification_id')
        
        if not notification_id:
            raise Exception('Devi specificare un\'user_id') 
        
        notification = Notification.get_by_id(int(notification_id))
        
        if notification:
            data = notification.payload
            notification.delete()
        else:
            data = json.dumps({'message': 'ok'})
    except Exception as e:
        logging.exception(e)
        data = json.dumps({'error': '%s' % str(e)})
        
    logging.info("data: %s"%data)
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(data)
