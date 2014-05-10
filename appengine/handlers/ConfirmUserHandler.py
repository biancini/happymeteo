'''
@author: Simon Vocella <voxsim@gmail.com>
'''

import webapp2
import os
import logging

from google.appengine.ext import db
from google.appengine.ext.webapp import template

class ConfirmUserHandler(webapp2.RequestHandler):
    def get(self):
        confirmation_code = self.request.get('confirmation_code')

        if not confirmation_code:
            logging.error("No confirmation code passed.")
            self.abort(500)
        
        q = db.GqlQuery("SELECT * FROM User WHERE confirmation_code = :1",
            confirmation_code)

        if q.count() > 0:
           user = q.get()
           user.status = 2
           user.confirmation_code = ""
           user.put()

           path = os.path.join(os.path.dirname(__file__), '..', 'templates', 'confirm_user.html')
           self.response.out.write(template.render(path, {
            'name': user.first_name,
            'email': user.email}))
        else:
           logging.error("Confirmation code not present in the DB.")
           self.abort(500)
