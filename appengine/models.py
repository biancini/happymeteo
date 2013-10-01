from google.appengine.ext import db

class Device(db.Model):
    registration_id = db.StringProperty()
    user_id = db.StringProperty()
    created = db.DateTimeProperty(auto_now_add=True)
    
class User(db.Model):
    facebook_id = db.StringProperty()
    first_name = db.StringProperty()
    last_name = db.StringProperty()
    gender = db.StringProperty()
    email = db.StringProperty()
    age = db.StringProperty()
    education = db.StringProperty()
    work = db.StringProperty()
    cap = db.StringProperty()
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
            'cap': self.cap,
            'registered': '1'
        }

class Challenge(db.Model):
    user_id_a = db.StringProperty() # sfidante
    user_id_b = db.StringProperty() # sfidato
    registration_id_a = db.StringProperty() # registration_id sfidante
    registration_id_b = db.StringProperty() # registration_id sfidato
    score_a = db.IntegerProperty()
    score_b = db.IntegerProperty()
    created = db.DateTimeProperty(auto_now_add=True)
    accepted = db.BooleanProperty(default=False)
    
    def toJson(self):
        return {
            'challenge_id': self.key().id(),
            'user_id_a': self.user_id_a,
            'user_id_b': self.user_id_b
        }
        
class Question(db.Model):
    question = db.StringProperty()
    type = db.IntegerProperty()
    order = db.IntegerProperty()
    
    def toJson(self):
        return {
            'id': self.key().id(),
            'question': self.question,
            'type': self.type
        }
        
class ChallengeQuestion(db.Model):
    question = db.StringProperty()
    
    def toJson(self):
        return {
            'id': self.key().id(),
            'question': self.question
        }
        
class Answer(db.Model):
    user_id = db.StringProperty()
    question_id = db.StringProperty()
    location = db.GeoPtProperty()
    date = db.DateTimeProperty()
    value = db.StringProperty()
    
class ChallengeAnswer(db.Model):
    user_id = db.StringProperty()
    question_id = db.StringProperty()
    location = db.GeoPtProperty()
    date = db.DateTimeProperty()
    value = db.StringProperty()
    challenge_id =  db.StringProperty()
    turn = db.StringProperty()