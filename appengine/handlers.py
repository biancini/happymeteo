# -*- coding: utf-8 -*-
import logging
import secrets
import sys
import json
import urllib2
from datetime import datetime

import webapp2
from webapp2_extras import auth, sessions, jinja2
from jinja2.runtime import TemplateNotFound

from google.appengine.ext import db

from models import User, Device

from secrets import GOOGLE_API_KEY

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
    """Head is used by Twitter. If not there the tweet button shows 0"""
    pass


class RootHandler(BaseRequestHandler):
  def get(self):
    """Handles default langing page"""
    self.render('home.html')

class FacebookLoginHandler(BaseRequestHandler):
  def calculate_age(self, birthday):
    today = date.today()
    try:
      birthday = born.replace(year=today.year)
    except ValueError: # raised when birth date is February 29 and the current year is not a leap year
      birthday = born.replace(year=today.year, day=born.day-1)
    if birthday > today:
      return today.year - born.year - 1
    else:
      return today.year - born.year

  # get_age: birthday in mm/gg/yyyy
  # 0 => 0 -24 years
  # 1 => 25 - 35 years
  # 2 => 35 - 50 years
  # 3 => > 50 years 
  def get_age(self, birthday):
    try:
      age = self.calculate_age(datetime.strptime(birthday, "%m/%d/%Y"))

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
    try:
      accessToken=self.request.get('accessToken')
      facebook_profile = json.load(urllib2.urlopen("https://graph.facebook.com/me?access_token=%s"%accessToken))

      data = {
        'facebook_id': facebook_profile['id'],
        'first_name': facebook_profile['first_name'],
        'last_name': facebook_profile['last_name'],
        'email':  facebook_profile['email'],
        'age': self.get_age(facebook_profile['birthday']),
        'education': '0',
        'work': '0',
        'location': facebook_profile['location']['name'],
        'registered': '0'
      }

      if facebook_profile['gender'] == "male":
        data['gender'] = 1
      else:
        data['gender'] = 0

      q = db.GqlQuery("SELECT * FROM User WHERE facebook_id = :1",
          facebook_profile['id'])

      if q.count() > 0:
        print "User already registered with facebook id = %s, update the informations"%facebook_profile['id']
        data['registered'] = '1'

      self.response.headers['Content-Type'] = 'application/json'
      self.response.out.write(json.dumps(data))
    except:
      data = {
        'error': 'Facebook Login error',
        'message': '%s'%sys.exc_info()[0],
        'type': 1
      }

      self.response.headers['Content-Type'] = 'application/json'
      self.response.out.write(json.dumps(data))
      raise

class CreateAccountHandler(BaseRequestHandler):
    def post(self):
      try:
        data = {}

        facebook_id=self.request.get('facebook_id')
        first_name=self.request.get('first_name')
        last_name=self.request.get('last_name')
        gender=self.request.get('gender')
        email=self.request.get('email')
        age=self.request.get('age')
        education=self.request.get('education')
        work=self.request.get('work')
        location=self.request.get('location')

        if facebook_id and facebook_id is not 0:
          print "facebook user"
          # already confirmed
          user = User(facebook_id=facebook_id, 
              first_name=first_name,
              last_name=last_name,
              gender=gender,
              email=email,
              age=age,
              education=education,
              work=work,
              location=location)
          user.put()
          
          data = {
            'message': 'OK',
          }
        else:
          # send an email to confirm the user
          print "normal user"
          data = {
            'message': 'TO_CONFIRM',
          }
        
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(data))
      except:
        data = {
          'error': 'Create Account error',
          'message': '%s'%sys.exc_info()[0],
          'type': 2
        }

        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(data))
        raise

class NormalLoginHandler(BaseRequestHandler):
    def post(self):
      # TODO
      print "normal login handler"

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
    registrationId=self.request.get('registrationId')
    q = db.GqlQuery("SELECT * FROM Device WHERE registrationId = :1", registrationId)

    if q.count() > 0:
      print "Device already registered with register id = %s"%registrationId
    else:
      n = Device(registrationId=registrationId)
      n.put()

class UnregisterHandler(BaseRequestHandler):

  def post(self):
    registrationId=self.request.get('registrationId')
    q = db.GqlQuery("SELECT * FROM Device WHERE registrationId = :1", registrationId)
    for p in q.run(limit=1):
      print "delete %s"%(p.registrationId)
      db.delete(p)
    return webapp2.redirect('/')

class SendMessageHandler(BaseRequestHandler):

  def get(self):
    registrationId=self.request.get('registrationId')
    data = {
        'registration_ids': [registrationId],
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

class GetQuestionsHandler(BaseRequestHandler):

  def post(self):
    # questions: 
    #  -> question: con il testo della domanda
    #  -> type:
    #      -> 1 -> [1-10]
    #      -> 2 -> Si/No
    questions = [
        {
          'question': 'Quanto ti senti felice, da uno 1 a 10?',
          'type': '1' 
          },
        {
          'question': 'Sei in compagnia?',
          'type': '2' 
          },
        {
          'question': 'Sei concentrato/a?',
          'type': '2' 
          }
        ]

    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(questions))
