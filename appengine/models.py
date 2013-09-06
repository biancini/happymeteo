from google.appengine.ext import db

class Device(db.Model):
    registration_id = db.StringProperty()
    user_id = db.StringProperty()
    
class User(db.Model):
    facebook_id = db.StringProperty()
    first_name = db.StringProperty()
    last_name = db.StringProperty()
    gender = db.StringProperty()
    email = db.StringProperty()
    age = db.StringProperty()
    education = db.StringProperty()
    work = db.StringProperty()
    location = db.StringProperty()
    confirmation_code = db.StringProperty()
    password = db.StringProperty()
    status = db.IntegerProperty()

    def toJson(self):
        return {
            'user_id': self.key().id(),
            'facebook_id': self.facebook_id,
            'first_name': self.first_name,
            'last_name': self.last_name,
            'email':  self.email,
            'gender':  self.gender,
            'age': self.age,
            'education': self.education,
            'work': self.work,
            'location': self.location,
            'registered': '1'
        }