import jinja2
import os
import webapp2
import json
import urllib2

from datetime import datetime
from google.appengine.ext import db

from models import Users

TEMPLATE_DIR = os.path.join(os.path.dirname(__file__), 'templates')
jinja_environment = \
    jinja2.Environment(loader=jinja2.FileSystemLoader(TEMPLATE_DIR))
GOOGLE_API_KEY = 'AIzaSyBeMxlRchiwXkyD36N9F2JpkmEXvEEnIVk'

class BaseHandler(webapp2.RequestHandler):

    @webapp2.cached_property
    def jinja2(self):
        return jinja2.get_jinja2(app=self.app)

    def render_template(
        self,
        filename,
        template_values,
        **template_args
        ):
        template = jinja_environment.get_template(filename)
        self.response.out.write(template.render(template_values))


class MainPage(BaseHandler):

    def get(self):
        users = Users.all()
        self.render_template('index.html', 
            {
              'users': users,
              'lenusers': db.Query(Users).count()
            })

class RegisterPage(BaseHandler):

    def post(self):
        regId=self.request.get('regId')
        name=self.request.get('name')
        email=self.request.get('email')

        n = Users(regId=regId, name=name, email=email)
        n.put()
    
    def get(self):
        """
        Do Nothing
        """

class UnregisterPage(BaseHandler):

    def post(self):
        regId=self.request.get('regId')

        q = db.GqlQuery("SELECT * FROM Users WHERE regId = :1", regId)
        for p in q.run(limit=1):
          print "delete %s %s"%(p.name, p.email)
          db.delete(p)
        return webapp2.redirect('/')
    
    def get(self):
        """
        Do Nothing
        """

class SendMessagePage(BaseHandler):
    
    def post(self):
        """ Do nothing """

    def get(self):
        regId=self.request.get('regId')
        data = {
          'registration_ids': [regId],
          'data': {
            'message': 'ciao'
          }
        }

        req = urllib2.Request('https://android.googleapis.com/gcm/send')
        req.add_header('Content-Type', 'application/json')
        req.add_header('Authorization', 'key=%s'%GOOGLE_API_KEY)

        print json.dumps(data)

        response = urllib2.urlopen(req, json.dumps(data))
        print response.read()

"""
class CreateNote(BaseHandler):

    def post(self):
        n = Notes(author=self.request.get('author'),
                  text=self.request.get('text'),
                  priority=self.request.get('priority'),
                  status=self.request.get('status'))
        n.put()
        return webapp2.redirect('/')

    def get(self):
        self.render_template('create.html', {})


class EditNote(BaseHandler):

    def post(self, note_id):
        iden = int(note_id)
        note = db.get(db.Key.from_path('Notes', iden))
        note.author = self.request.get('author')
        note.text = self.request.get('text')
        note.priority = self.request.get('priority')
        note.status = self.request.get('status')
        note.date = datetime.now()
        note.put()
        return webapp2.redirect('/')

    def get(self, note_id):
        iden = int(note_id)
        note = db.get(db.Key.from_path('Notes', iden))
        self.render_template('edit.html', {'note': note})


class DeleteNote(BaseHandler):

    def get(self, note_id):
        iden = int(note_id)
        note = db.get(db.Key.from_path('Notes', iden))
        db.delete(note)
        return webapp2.redirect('/')
"""
