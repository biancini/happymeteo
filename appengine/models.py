from google.appengine.ext import db

class Device(db.Model):
    registrationId = db.StringProperty()

class User(db.Model):
    facebook_id = db.StringProperty()
