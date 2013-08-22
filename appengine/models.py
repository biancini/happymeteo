from google.appengine.ext import db

class Device(db.Model):
    registrationId = db.StringProperty()

#TODO With 64 bit: Gender-1|age-2|education-2|work-2
class User(db.Model):
    facebook_id = db.StringProperty()
    first_name = db.StringProperty()
    last_name = db.StringProperty()
    gender = db.StringProperty()
    email = db.StringProperty()
    age = db.StringProperty()
    education = db.StringProperty()
    work = db.StringProperty()
