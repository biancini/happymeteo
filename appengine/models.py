from google.appengine.ext import db

class Users(db.Model):
    regId = db.StringProperty()
    name = db.StringProperty()
    email = db.StringProperty()
