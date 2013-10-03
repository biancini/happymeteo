# -*- coding: utf-8 -*-
import sys
import json
import urllib2

import webapp2
from webapp2_extras import auth, sessions, jinja2
from jinja2.runtime import TemplateNotFound

from google.appengine.ext import db

from models import User, Device, Challenge, Question, ChallengeQuestion, Answer, \
    ChallengeAnswer, ChallengeQuestionCategory

from secrets import EMAIL, CREATE_ACCOUNT_EMAIL

from utils import sendMessage, happymeteo, sample, check_call

import traceback
import logging

from datetime import datetime

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

def check_hash(handler_method):
    def check_hash(self, *args, **kwargs):
        if not check_call(self.request):
            data = {
              'error': 'access-denied',
              'message': 'Accesso Negato'
            }
            
            self.response.headers['Content-Type'] = 'application/json'
            self.response.out.write(json.dumps(data))
        else:
            handler_method(self, *args, **kwargs)
    return check_hash

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

  @check_hash
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
        (today_value, yesterday_value, tomorrow_value) = happymeteo(data['user_id'])
        data['today'] = today_value
        data['yesterday'] = yesterday_value
        data['tomorrow'] = tomorrow_value
      else:
        data = {
            'user_id': '',
            'facebook_id': facebook_profile['id'],
            'first_name': facebook_profile['first_name'],
            'last_name': facebook_profile['last_name'],
            'email':  facebook_profile['email'],
            'age': self.get_age(facebook_profile['birthday']),
            'education': '0',
            'cap': '',
            'work': '0',
            'registered': '0'
        }
        
        if facebook_profile['gender'] == "male":
          data['gender'] = 1
        else:
          data['gender'] = 0

    except Exception as e:
      logging.exception(e)
      data = {
        'error': 'Facebook Login error',
        'message': '%s' % str(e)
      }
      
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))

class CreateAccountHandler(BaseRequestHandler):
  @check_hash
  def post(self):
      data = {}
      
      try:
        user_id = self.request.get('user_id')
        facebook_id = self.request.get('facebook_id')
        first_name = self.request.get('first_name')
        last_name = self.request.get('last_name')
        gender = self.request.get('gender')
        email = self.request.get('email')
        age = self.request.get('age')
        education = self.request.get('education')
        work = self.request.get('work')
        cap = self.request.get('cap')
        password = self.request.get('password')
        
        if user_id == "":
            query = User.gql('WHERE email = :1', email)
            
            if query.count() == 0:
                user = User(facebook_id=facebook_id,
                    first_name=first_name,
                    last_name=last_name,
                    gender=gender,
                    email=email,
                    age=age,
                    education=education,
                    work=work,
                    cap=cap,
                    status=0,
                    password=password,
                    contatore_impulsi=0,
                    contatore_sfidante=0,
                    contatore_sfidato=0,
                    contatore_amici_invitati=0)
        
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
                                              subject="Conferma del tuo account su Appy Meteo")
                  message.to = "%s %s <%s>" % (first_name, last_name, email)
                  message.body = CREATE_ACCOUNT_EMAIL % (first_name, user.confirmation_code)
                  message.send()
                
                user.put()
                data['user_id'] = user.key().id()
            else:
                data = {
                  'error': 'Create Account error',
                  'message': 'user with same email already exists',
                }
        else:
            ok = False
            user = User.get_by_id(int(user_id))
            
            if facebook_id != "":
                query = User.gql('WHERE facebook_id = :1', facebook_id)
                
                if query.count() == 0:
                    ok = True
                else:
                    user2 = query.get()
                    if user.key().id() != user2.key().id():
                        data = {
                          'error': 'Create Account error',
                          'message': 'user with same facebook account already exists',
                        }
                    else:
                        ok = True
            else:
                ok = True
                
            if ok:
                user.facebook_id = facebook_id
                user.first_name = first_name
                user.last_name = last_name
                user.gender = gender
                user.email = email
                user.age = age
                user.education = education
                user.work = work
                user.cap = cap
                user.put()
                data = {
                    'message': 'CONFIRMED_OR_FACEBOOK',
                    'user_id': user.key().id()
                }
      except Exception as e:
        logging.exception(e)
        data = {
          'error': 'Create Account error',
          'message': '%s' % traceback.format_exc(),
        }
    
      self.response.headers['Content-Type'] = 'application/json'
      self.response.out.write(json.dumps(data))

class NormalLoginHandler(BaseRequestHandler):
  @check_hash
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
            
            (today_value, yesterday_value, tomorrow_value) = happymeteo(data['user_id'])
            data['today'] = today_value
            data['yesterday'] = yesterday_value
            data['tomorrow'] = tomorrow_value
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
      except Exception as e:
        logging.exception(e)
        data = {
          'error': 'Normal Login error',
          'message': '%s' % str(e)
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
class RegisterHandler(BaseRequestHandler):
  @check_hash
  def post(self):
    registrationId = self.request.get('registrationId')
    userId = self.request.get('userId')
    query = Device.gql("WHERE user_id = :1 AND registration_id = :2", userId, registrationId)
    
    if query.count() == 0:
       device = Device(registration_id=registrationId, user_id=userId)
       device.put()
      
    data = { 'message': 'ok' }
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))

class UnregisterHandler(BaseRequestHandler):

  @check_hash
  def post(self):
    registrationId = self.request.get('registrationId')
    query = db.GqlQuery("SELECT * FROM Device WHERE registration_id = :1", registrationId)
    for device in query.run(limit=1):
      print "delete %s" % (device.registration_id)
      db.delete(device)
    
    data = { 'message': 'ok' }
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))

class SendMessageHandler(BaseRequestHandler):

  def get(self):
    import time
    ts = time.time()
    
    devices = Device.all()
    
    for d in devices:
        sendMessage(d.registration_id, collapse_key='questions', payload={'user_id': d.user_id, 'timestamp': '%s'%ts})
        print '%s'%ts

""" Questions Management """
class GetQuestionsHandler(BaseRequestHandler):

  @check_hash
  def post(self):
    questions = Question.gql("Order BY order")
    
    if questions.count() > 0:
       self.response.headers['Content-Type'] = 'application/json'
       self.response.out.write(json.dumps([q.toJson() for q in questions]))

class SubmitQuestionsHandler(BaseRequestHandler):

  @check_hash
  @check_hash
  def post(self):
    data = {}
    try:
        questions = self.request.get('questions')
        user_id = self.request.get('user_id')
        latitude = self.request.get('latitude')
        longitude = self.request.get('longitude')
        timestamp = self.request.get('timestamp')
        
        if not timestamp or timestamp == "":
            raise Exception('You need to specify the timestamp')
        
        answers = Answer.gql("WHERE user_id = :1 AND timestamp = :2", user_id, timestamp)
        
        if answers.count() > 0:
            raise Exception('You already answer this impulse')
        
        user = User.get_by_id(int(user_id))
        user.contatore_impulsi = user.contatore_impulsi + 1
        user.put()
        
        questions = json.loads(questions)
        for q in questions:
            answer = Answer(
                user_id=user_id,
                question_id=q,
                date=datetime.now(),
                value=questions[q],
                timestamp=timestamp)
            
            if latitude and longitude:
               answer.location = latitude + "," + longitude
            
            answer.put()
        
        data = { 'message': 'ok' }
        (today_value, yesterday_value, tomorrow_value) = happymeteo(int(user_id))
        data['today'] = today_value
        data['yesterday'] = yesterday_value
        data['tomorrow'] = tomorrow_value
    except Exception as e:
        logging.exception(e)
        data = {
          'error': 'Submit Question error',
          'message': '%s' % str(e)
        }
        
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
    
""" Challenge Management """
class RequestChallengeHandler(BaseRequestHandler):
    
  @check_hash
  def post(self):
    try:
        userId = self.request.get('userId')
        registrationId = self.request.get('registrationId')
        facebookId = self.request.get('facebookId')
        
        query1 = db.GqlQuery("SELECT * FROM User WHERE facebook_id = :1", str(facebookId))
        data = {}
        
        if query1.count() == 0:
            raise Exception('Nessun utente trovato')
        
        user = query1.get()
        if userId == str(user.key().id()):
            raise Exception('Non puoi sfidare te stesso')
        
        query2 = db.GqlQuery("SELECT * FROM Device WHERE user_id = :1", str(user.key().id()))
            
        if query2.count() == 0:
            raise Exception('Nessun device trovato')
        
        # Save challenge
        query = Challenge.gql("WHERE user_id_a = :1 and user_id_b = :2 and accepted = false and turn = 0", userId, '%s' % user.key().id())
        add = False
        if query.count() > 0:
          challenge = query.get()
          
          if challenge:
              challenge.registration_id_a = registrationId
              challenge.created = datetime.now()
              challenge.turn = 0
              challenge.put()
          else:
              add = True
        else:
          add = True
          
        if add:
          challenge = Challenge(user_id_a=userId, user_id_b='%s'%user.key().id(), registration_id_a=registrationId, accepted=False)
          challenge.put()
          
        # Send request to all devices of user_b
        for device in query2.run():
            sendMessage(device.registration_id, collapse_key='request_challenge', payload={'user_id': challenge.user_id_b, 'challenge_id': '%s'%challenge.key().id()})
        
        data = {
          'message': 'ok'
        }
    except Exception as e:
        logging.exception(e)
        data = {
          'error': 'Request Challenge error',
          'message': '%s' % str(e)
        }
        
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
    
class AcceptChallengeHandler(BaseRequestHandler):
    
  @check_hash
  def post(self):
    try:
        challengeId = self.request.get('challengeId')
        accepted = self.request.get('accepted')
        registrationId = self.request.get('registrationId')
        userId = self.request.get('userId')
        
        data = {}
        
        challenge = Challenge.get_by_id(int(challengeId))
    
        if not challenge:
            raise Exception('Nessuna sfida trovata')
        
        if challenge.user_id_b != userId:
            raise Exception('C\'è stato un errore con la sfida')
        
        if challenge.turn > 0:
            raise Exception('Sfida scaduta')
        
        query2 = db.GqlQuery("SELECT * FROM Device WHERE registration_id = :1 and user_id = :2", registrationId, userId)
            
        if query2.count() == 0:
            raise Exception('Nessun device trovato')
        
        user_a = User.get_by_id(int(challenge.user_id_a))
        user_a.contatore_sfidante = user_a.contatore_sfidante + 1
        user_a.put()
        
        user_b = User.get_by_id(int(challenge.user_id_b))
        user_b.contatore_sfidato = user_b.contatore_sfidato + 1
        user_b.put()
        
        sendMessage(challenge.registration_id_a, payload={'user_id': challenge.user_id_a, 'appy_key': 'accepted_challenge_turn1_%s' % accepted, 'challenge_id': challenge.key().id(), 'turn': '1'})
        challenge.accepted = (accepted == "true")
        challenge.registration_id_b = registrationId,
        challenge.turn = 1
        challenge.put()
        
        data = {
          'message': 'ok'
        }
    except Exception as e:
        logging.exception(e)
        data = {
          'error': 'Accept Challenge error',
          'message': '%s' % str(e)
        }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
    
class QuestionsChallengeHandler(BaseRequestHandler):

  @check_hash
  def post(self):
    challengeId = self.request.get('challengeId')
    turn = self.request.get('turn')
    
    if turn == "1":
        import random
        
        questions = ChallengeQuestion.gql("WHERE category_id = 0")
        questions_json = [questions.get().toJson()]
        
        categories = ChallengeQuestionCategory.all()
        for c in categories:
            questions = ChallengeQuestion.gql("WHERE category_id = :1", c.key().id())
            question = sample(random.random, questions, 1)
            questions_json.append(question[0].toJson())
        
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(questions_json))
    else:
        answers = ChallengeAnswer.gql("WHERE challenge_id = :1", challengeId)
        questions = [ChallengeQuestion.get_by_id(int(answer.question_id)) for answer in answers]
        questions.sort(key=lambda x: x.order, reverse=False)
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps([q.toJson() for q in questions]))
        
"""        
  def get(self):
      questions = ChallengeQuestion.all()
      
      for q in questions:
          q.delete()
          
      questions = ChallengeQuestionCategory.all()
      
      for q in questions:
          q.delete()
          
      q1 = ChallengeQuestion(question="In termini generali quanto ti senti felice?", type=1, weight=1.0, textYes="Si", textNo="No", category_id=0, order=0)
      q1.put()
      
      c1 = ChallengeQuestionCategory(name="Stili di vita")
      c1.put()
      
      questions = ["Come hai mangiato oggi?", "Ti senti in forma?", "Sei soddisfatto/a di come ti muovi per la citt&agrave;?", "Ti piace come sei vestita/o?",
                   "Sei contento/a per un acquisto fatto?", "Sei contento/a di quel che bevi?", "Sei contento/a di come usi Internet?", "Sei contento/a del tuo lavoro?",
                   "Sei contento/a della tua vita sessuale?", "Sei contento/a della tua attivit&agrave; sportiva?", "Ti trovi bene con gli altri?"]
      
      for q in questions:
          print q
          q1 = ChallengeQuestion(question=q, type=2, weight=1.0, textYes="Si", textNo="No", category_id=c1.key().id(), order=1)
          q1.put()
      
      c2 = ChallengeQuestionCategory(name="Episodi quotidiani")
      c2.put()
      
      questions = ["Sei contento/a delle notizie di oggi?", "Sei contento/a del tempo che fa?", "Sei contento/a degli incontri fatti oggi?",
                   "Sei contento/a della musica ascoltata oggi?", "Sei contento/a delle idee avute oggi?", "Sei contento/a di come &egrave; andata al lavoro oggi?"]
      
      for q in questions:
          print q
          q1 = ChallengeQuestion(question=q, type=2, weight=1.0, textYes="Si", textNo="No", category_id=c2.key().id(), order=1)
          q1.put()
      
      c3 = ChallengeQuestionCategory(name="Stati emotivi e psico-fisici")
      c3.put()
      
      questions = ["Sei arrabbiata/o?", "Sei confuso/a?", "Sei stressato/a?", "Sei teso/a?", "Sei nervoso/a?", "Sei malinconico/a?", "Sei stanco/a?",
                   "Sei depresso/a?", "Sei allegro/a?", "Sei spaventato/a?", "Sei curioso/a?", "Sei ottimista?", "Sei pessimista?", "Sei fiducioso/a?"]
      
      for q in questions:
          print q
          q1 = ChallengeQuestion(question=q, type=2, weight=1.0, textYes="Si", textNo="No", category_id=c3.key().id(), order=1)
          q1.put()
      
      c4 = ChallengeQuestionCategory(name="Analisi retrospettiva")
      c4.put()
      
      questions = ["Eri contento/a ieri?", "Ti sentivi bene la settimana scorsa?", "Sei contento/a dell'anno passato?"]
      
      for q in questions:
          print q
          q1 = ChallengeQuestion(question=q, type=2, weight=1.0, textYes="Si", textNo="No", category_id=c4.key().id(), order=1)
          q1.put()
"""

class SubmitChallengeHandler(BaseRequestHandler):

  @check_hash
  def post(self):
    data = {}
    try:
        challenge_id = self.request.get('challenge_id')
        turn = self.request.get('turn')
        questions = self.request.get('questions')
        user_id = self.request.get('user_id')
        longitude = self.request.get('longitude')
        latitude = self.request.get('latitude')
        
        challenge = Challenge.get_by_id(int(challenge_id))
    
        if not challenge:
            raise Exception('Sfida non trovata')
        
        if not challenge.accepted:
            raise Exception('C\'è stato un errore con la sfida')
        
        if turn != "1" and challenge.turn == 1:
            raise Exception('C\'è stato un errore con la sfida')
        
        if turn != "2" and challenge.turn == 2:
            raise Exception('C\'è stato un errore con la sfida')
        
        questions = json.loads(questions)
        score = 0
        
        for q in questions:
            print "id q: %s"%q
            question = ChallengeQuestion.get_by_id(int(q))
            print "question: %s"%question
            
            score = score + float(questions[q]) * question.weight
            
            challengeAnswer = ChallengeAnswer(
                user_id=user_id,
                question_id=q,
                date=datetime.now(),
                value=questions[q],
                challenge_id=challenge_id,
                turn=turn)
            
            if latitude and longitude:
               challengeAnswer.location = latitude + "," + longitude
            
            challengeAnswer.put()
            
        # calcolare lo score
        data = {'score': score}
        
        # aggiornare il challenge & Se primo turno manda la notifica a utente b o b manda la fine ad a
        if turn == "1":
            challenge.score_a = float(score)
            challenge.turn = 2
            sendMessage(challenge.registration_id_b, payload={'user_id': challenge.user_id_b, 'appy_key': 'accepted_challenge_turn2', 'score': score, 'challenge_id': challenge.key().id(), 'turn': '2'})
        else:
            challenge.score_b = float(score)
            challenge.turn = 3
            sendMessage(challenge.registration_id_a, payload={'user_id': challenge.user_id_a, 'appy_key': 'accepted_challenge_turn3', 'ioChallenge': challenge.score_a, 'tuChallenge': score, 'challenge_id': challenge.key().id(), 'turn': '3'})
            
        challenge.put()
    except Exception as e:
       logging.exception(e)
       data = {
         'error': 'Submit Challenge error',
          'message': '%s' % str(e)
       }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
