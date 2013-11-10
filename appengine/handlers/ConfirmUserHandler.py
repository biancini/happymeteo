'''
@author: Simon Vocella <voxsim@gmail.com>
'''
from google.appengine.ext import db
import webapp2

class ConfirmUserHandler(webapp2.RequestHandler):
    def get(self):
        confirmation_code = self.request.get('confirmation_code')
        
        if not confirmation_code:
            self.abort(500)
        
        q = db.GqlQuery("SELECT * FROM User WHERE confirmation_code = :1",
            confirmation_code)

        if q.count() > 0:
           user = q.get()
           user.status = 2
           user.confirmation_code = ""
           user.put()
           self.render('confirm_user.html', template_vars={
            'name': user.first_name,
            'email': user.email})
        else:
           self.abort(500)