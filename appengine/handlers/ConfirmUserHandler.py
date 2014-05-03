'''
@author: Simon Vocella <voxsim@gmail.com>
'''
from google.appengine.ext import db
from google.appengine.ext.webapp import template
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

           path = os.path.join(os.path.dirname(__file_), '..', 'templates', 'confirm_user.html')
           template.render(path, template_vars={
            'name': user.first_name,
            'email': user.email})
        else:
           self.abort(500)
