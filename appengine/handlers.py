# -*- coding: utf-8 -*-
import sys
import json
import urllib2
from datetime import datetime

import webapp2
from webapp2_extras import auth, sessions, jinja2
from jinja2.runtime import TemplateNotFound

from google.appengine.ext import db

from models import User, Device, Challenge

from secrets import EMAIL

from utils import sendToSyncMessage

class BaseRequestHandler(webapp2.RequestHandler):
  def dispatch(self):
    # Get a session store for this request.
    self.session_store = sessions.get_store(request=self.request)

    try:
      # Dispatch the request.
      webapp2.RequestHandler.dispatch(self)
    finally:
      # Save all sessions.
      self.session_store.save_sessions(self.response)

  @webapp2.cached_property    
  def jinja2(self):
    """Returns a Jinja2 renderer cached in the app registry"""
    return jinja2.get_jinja2(app=self.app)

  @webapp2.cached_property
  def session(self):
    """Returns a session using the default cookie key"""
    return self.session_store.get_session()

  @webapp2.cached_property
  def auth(self):
    return auth.get_auth()

  @webapp2.cached_property
  def current_user(self):
    """Returns currently logged in user"""
    user_dict = self.auth.get_user_by_session()
    return self.auth.store.user_model.get_by_id(user_dict['user_id'])

  @webapp2.cached_property
  def logged_in(self):
    """Returns true if a user is currently logged in, false otherwise"""
    return self.auth.get_user_by_session() is not None

  def render(self, template_name, template_vars={}):
    # Preset values for the template
    values = {
        'url_for': self.uri_for,
        'logged_in': self.logged_in,
        'flashes': self.session.get_flashes()
        }

    # Add manually supplied template values
    values.update(template_vars)

    # read the template or 404.html
    try:
      self.response.write(self.jinja2.render_template(template_name, **values))
    except TemplateNotFound:
      self.abort(404)

  def head(self, *args):
    pass

class RootHandler(BaseRequestHandler):
  def get(self):
    """Handles default langing page"""
    self.render('home.html')

""" Profile user """
class FacebookLoginHandler(BaseRequestHandler):
  def calculate_age(self, born):
    today = datetime.date.today()
    try:
      birthday = born.replace(year=today.year)
    except ValueError:  # raised when birth date is February 29 and the current year is not a leap year
      birthday = born.replace(year=today.year, day=born.day - 1)
    if birthday > today:
      return today.year - born.year - 1
    else:
      return today.year - born.year

  # get_age: birthday in mm/gg/yyyy
  # 0 => 0 -24 years
  # 1 => 25 - 35 years
  # 2 => 35 - 50 years
  # 3 => > 50 years 
  def get_age(self, born):
    try:
      age = self.calculate_age(datetime.strptime(born, "%m/%d/%Y"))

      if age < 25:
        return 0

      if age < 36:
        return 1

      if age < 51:
        return 2

      return 3
    except:
      return 0

  def post(self):
    data = {}
    
    try:
      accessToken = self.request.get('accessToken')
      facebook_profile = json.load(urllib2.urlopen("https://graph.facebook.com/me?access_token=%s" % accessToken))
 
      query = db.GqlQuery("SELECT * FROM User WHERE facebook_id = :1",
          facebook_profile['id'])

      if query.count() > 0:
        user = query.get()
        data = user.toJson()
      else:
        data = {
            'user_id': '',
            'facebook_id': facebook_profile['id'],
            'first_name': facebook_profile['first_name'],
            'last_name': facebook_profile['last_name'],
            'email':  facebook_profile['email'],
            'age': self.get_age(facebook_profile['birthday']),
            'education': '0',
            'location': '',
            'work': '0',
            'registered': '0'
        }
        
        try: data['location'] = facebook_profile['location']['name']
        except KeyError: pass
        
        if facebook_profile['gender'] == "male":
          data['gender'] = 1
        else:
          data['gender'] = 0

    except:
      data = {
        'error': 'Facebook Login error',
        'message': '%s' % sys.exc_info()[0],
      }
      
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))

class CreateAccountHandler(BaseRequestHandler):
    def post(self):
      data = {}
      
      try:
        facebook_id = self.request.get('facebook_id')
        first_name = self.request.get('first_name')
        last_name = self.request.get('last_name')
        gender = self.request.get('gender')
        email = self.request.get('email')
        age = self.request.get('age')
        education = self.request.get('education')
        work = self.request.get('work')
        location = self.request.get('location')
        password = self.request.get('password')
          
        user = User(facebook_id=facebook_id,
            first_name=first_name,
            last_name=last_name,
            gender=gender,
            email=email,
            age=age,
            education=education,
            work=work,
            location=location,
            status=0,
            password=password)

        if facebook_id and facebook_id != "0":
          # already confirmed
          data = {
            'message': 'CONFIRMED_OR_FACEBOOK',
          }
          user.status = 2
        else:
          # send an email to confirm the user
          data = {
            'message': 'NOT_CONFIRMED',
          }
          import os
          user.confirmation_code = os.urandom(32).encode('hex')
          user.status = 1

          from google.appengine.api import mail
          message = mail.EmailMessage(sender="happymeteo <%s>" % EMAIL,
                                      subject="Conferma del tuo account su Happy Meteo")

          message.to = "%s %s <%s>" % (first_name, last_name, email)
          message.body = """
Benvenuto %s,

Il tuo account su Happy Meteo ha bisogno di essere verificato, per
farlo clicca sul link sottostante:
https://happymeteo.appspot.com/confirm_user?confirmation_code=%s

Saluti,
Happy Meteo Team
          """ % (first_name, user.confirmation_code)

          message.send()
        
        user.put()
        data['user_id'] = user.key().id()
      except:
        data = {
          'error': 'Create Account error',
          'message': '%s' % sys.exc_info()[0],
        }
        raise
    
      self.response.headers['Content-Type'] = 'application/json'
      self.response.out.write(json.dumps(data))

class NormalLoginHandler(BaseRequestHandler):
    def post(self):
      data = {}
      
      try:
        email = self.request.get('email')
        pwd_parameter = self.request.get('password')
        q = db.GqlQuery("SELECT * FROM User WHERE email = :1 and status = 2", email)

        if q.count() > 0:
          user = q.get()

          if user.password == pwd_parameter:
            data = user.toJson()
          else:
            data = {
              'error': 'Normal Login error',
              'message': 'Password didn\'t match',
            }
        else:
          data = {
            'error': 'Normal Login error',
            'message': 'User not found or not confirmed',
          }
      except:
        data = {
          'error': 'Normal Login error',
          'message': '%s' % sys.exc_info()[0],
        }
      
      self.response.headers['Content-Type'] = 'application/json'
      self.response.out.write(json.dumps(data))

class ConfirmUserHandler(BaseRequestHandler):
    def get(self):
        confirmation_code = self.request.get('confirmation_code')
        q = db.GqlQuery("SELECT * FROM User WHERE confirmation_code = :1",
            confirmation_code)

        if q.count() > 0:
          user = q.get()
          user.status = 2
          user.confirmation_code = ""
          user.put()

        self.response.out.write("User confirmed")
        
""" Device Management """
class IndexDeviceHandler(BaseRequestHandler):
  def get(self):
    devices = Device.all()
    self.render('index_device.html',
      {
        'devices': devices,
        'lendevices': db.Query(Device).count()
      })

class RegisterHandler(BaseRequestHandler):
  def post(self):
    registrationId = self.request.get('registrationId')
    userId = self.request.get('userId')
    query = Device.gql("WHERE user_id = :1", userId)

    if query.count() > 0:
      device = query.get()
      if device.registrationId != registrationId:
        device.registrationId = registrationId
        device.put()
    else:
      device = Device(registration_id=registrationId, user_id=userId)
      device.put()

class UnregisterHandler(BaseRequestHandler):

  def post(self):
    registrationId = self.request.get('registrationId')
    query = db.GqlQuery("SELECT * FROM Device WHERE registration_id = :1", registrationId)
    for device in query.run(limit=1):
      print "delete %s" % (device.registration_id)
      db.delete(device)
    return webapp2.redirect('/')

class SendMessageHandler(BaseRequestHandler):

  def get(self):
    registrationId = self.request.get('registrationId')
    sendToSyncMessage(registrationId, 'questions')

""" Questions Management """
class GetQuestionsHandler(BaseRequestHandler):

  def post(self):
    req = urllib2.Request('https://www.googleapis.com/fusiontables/v1/query?sql=SELECT%20*%20FROM%201x82FO5LkeHto6NfHJedrXUtcTl8QkSxoqxelpkI&key=AIzaSyBeMxlRchiwXkyD36N9F2JpkmEXvEEnIVk')
    req.add_header('Content-Type', 'application/json')
    # req.add_header('Authorization', 'key=%s'%GOOGLE_API_KEY)
    response = urllib2.urlopen(req)
    questions_list = json.loads(response.read())
    questions = []
    
    for question in questions_list['rows']:
      questions.append({
          'id': question[0],
          'question': question[1],
          'type': question[2]
      })
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(questions))

class SubmitQuestionsHandler(BaseRequestHandler):

  def post(self):
    # questions: 
    #  -> question: con il testo della domanda
    #  -> type:
    #      -> 1 -> [1-10]
    #      -> 2 -> Si/No
    ok = { 'message': 'ok' }
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(ok))
    
""" Challenge Management """
class RequestChallengeHandler(BaseRequestHandler):
    
  def post(self):
    userId = self.request.get('userId')
    registrationId = self.request.get('registrationId')
    facebookId = self.request.get('facebookId')
    
    query1 = db.GqlQuery("SELECT * FROM User WHERE facebook_id = :1", str(facebookId))
    data = {}
    
    if query1.count() > 0:
        user = query1.get()
        query2 = db.GqlQuery("SELECT * FROM Device WHERE user_id = :1", str(user.key().id()))
        
        if query2.count() > 0:
            # Get the device
            device = query2.get()
            
            # Save challenge
            query = Challenge.gql("WHERE user_id_a = :1 and user_id_b = :2 and accepted = false", userId, '%s'%user.key().id())
            if query.count() > 0:
              challenge = query.get()
              challenge.registration_id_a = registrationId
              challenge.registration_id_b = device.registration_id
              challenge.created = datetime.now()
              challenge.put()
            else:
              challenge = Challenge(user_id_a = userId, user_id_b = '%s'%user.key().id(), registration_id_a = registrationId, registration_id_b = device.registration_id, accepted = False)
              challenge.put()
            
            # Send message to the device
            sendToSyncMessage(device.registration_id, 'request_challenge', {'challenge': challenge.toJson()})
            data = {
              'message': 'ok',
              'challenge': challenge.toJson()
            }
        else:
            data = {
              'error': 'Send Message error',
              'message': 'No device found'
            }
    else:
        data = {
          'error': 'Send Message error',
          'message': 'No user found'
        }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
    
class AcceptChallengeHandler(BaseRequestHandler):
    
  def post(self):
    challengeId = self.request.get('challengeId')
    accepted = self.request.get('accepted')
    
    print "accepted: %s"%accepted
    
    data = {}
    challenge = Challenge.get_by_id(int(challengeId))
    
    if challenge:
        #sendToSyncMessage(challenge.registration_id_a, 'accepted_challenge', {'accept': accept})
        challenge.accepted = (accepted == "true")
        challenge.put()
        data = {
          'message': 'ok'
        }
    else:
        data = {
          'error': 'Accept Challenge error',
          'message': 'No challenge found'
        }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
    
class SendScoreChallengeHandler(BaseRequestHandler):
    
  def post(self):
    userId = self.request.get('userId')
    challengeId = self.request.get('challengeId')
    score = self.request.get('score')
    
    user = User.get_by_id(userId)
    data = {}
    
    if user:
      challenge = Challenge.get_by_id(challengeId)
      
      if challenge:
        if userId == challenge.user_id_a:
            challenge.score_a = score
        if userId == challenge.user_id_b:
            challenge.score_b = score
        
        challenge.put()
        data = {
          'message': 'ok'
        }
      else:
        data = {
          'error': 'Accept Challenge error',
          'message': 'No challenge found'
        }
    else:
        data = {
          'error': 'Accept Challenge error',
          'message': 'No user found'
        }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
