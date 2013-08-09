# -*- coding: utf-8 -*-
import logging
import secrets

import webapp2
from webapp2_extras import auth, sessions, jinja2
from jinja2.runtime import TemplateNotFound

from datetime import datetime
from google.appengine.ext import db
import json
import urllib2
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
    
class ProfileHandler(BaseRequestHandler):
  def get(self):
    """Handles GET /profile"""    
    if self.logged_in:
      self.render('profile.html', {
        'user': self.current_user, 
        'session': self.auth.get_user_by_session()
      })
    else:
      self.redirect('/')

class FacebookLoginHandler(BaseRequestHandler):

    def post(self):
        accessToken=self.request.get('accessToken')
        facebook_profile = json.load(urllib2.urlopen("https://graph.facebook.com/me?access_token=%s"%accessToken))
        
        data = {
          'facebook_id': facebook_profile['id'],
          'first_name': facebook_profile['first_name'],
          'last_name': facebook_profile['last_name'],
          'gender': facebook_profile['gender'],
          'birthday': 'manca',
          'education': '?',
          'work': '?',
          # citta di residenza
          'location': facebook_profile['location']['name'],
          'registered': '0'
        }

        q = db.GqlQuery("SELECT * FROM User WHERE facebook_id = :1",
            facebook_profile['id'])

        if q.count() > 0:
          print "User already registered with facebook id = %s, update the informations"%facebook_profile['id']
          data['registered'] = '1'
        else:
          n = User(facebook_id=facebook_profile['id'])
          n.put()

        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(data))

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
    
    def get(self):
        """
        Do Nothing
        """

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
