# -*- coding: utf-8 -*-
import json
import urllib2

import webapp2
from webapp2_extras import auth, sessions, jinja2
from jinja2.runtime import TemplateNotFound

from google.appengine.ext import db

from models import User, Device, Challenge, Question, ChallengeQuestion, Answer, \
    ChallengeAnswer, ChallengeQuestionCategory, Region, Provincia, Marker

from secrets import EMAIL, CREATE_ACCOUNT_EMAIL, CHANGE_FACEBOOK_EMAIL,\
    LOST_PASSWORD_EMAIL

from utils import sendMessage, happymeteo, sample, check_call,\
    point_inside_polygon, send_new_password, mkFirstOfMonth, mkLastOfMonth,\
    mkDateTime, formatDate

import logging

from datetime import datetime, date, timedelta

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
    except TemplateNotFound as tnf:
      logging.exception(tnf)
      self.abort(500)

  def head(self, *args):
    pass

def check_hash(handler_method):
    def check_hash(self, *args, **kwargs):
        if not check_call(self.request):
            data = {
              'error': 'access-denied'
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
        'error': '%s' % str(e)
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
                if user.status == 2:
                    (today_value, yesterday_value, tomorrow_value) = happymeteo(data['user_id'])
                    data['today'] = today_value
                    data['yesterday'] = yesterday_value
                    data['tomorrow'] = tomorrow_value
            else:
                data = {
                  'error': 'user with same email already exists'
                }
        else:
            addUser = False
            user = User.get_by_id(int(user_id))
            
            if facebook_id != "":
                query = User.gql('WHERE facebook_id = :1', facebook_id)
                
                if query.count() == 0:
                    addUser = True
                else:
                    user2 = query.get()
                    if user.key().id() != user2.key().id():
                        data = {
                          'error': 'user with same facebook account already exists'
                        }
                    else:
                        addUser = True
            else:
                addUser = True
                
            if addUser:
                # utente che si scollega da facebook e non ha una password
                if user.facebook_id != "" and facebook_id == "" and user.password == "":
                    new_password = send_new_password(first_name, last_name, email, CHANGE_FACEBOOK_EMAIL)
                    user.password = new_password
                
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
                (today_value, yesterday_value, tomorrow_value) = happymeteo(user.key().id())
                data = {
                    'message': 'CONFIRMED_OR_FACEBOOK',
                    'user_id': user.key().id(),
                    'today': today_value,
                    'yesterday': yesterday_value,
                    'tomorrow': tomorrow_value
                }
      except Exception as e:
        logging.exception(e)
        data = {
          'error': '%s' % str(e)
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
              'error': 'Password didn\'t match'
            }
        else:
          data = {
            'error': 'User not found or not confirmed'
          }
      except Exception as e:
        logging.exception(e)
        data = {
          'error': '%s' % str(e)
        }
      
      self.response.headers['Content-Type'] = 'application/json'
      self.response.out.write(json.dumps(data))

class ConfirmUserHandler(BaseRequestHandler):
    def get(self):
        confirmation_code = self.request.get('confirmation_code')
        
        if not confirmation_code:
            self.abort(500)
        
        q = db.GqlQuery("SELECT * FROM User WHERE confirmation_code = :1",
            confirmation_code)

        if q.count() > 0:
           user = q.get()
           user.status = 2
           user.confirmation_code = ""
           user.put()
           self.render('confirm_user.html', template_vars={
            'name': user.first_name,
            'email': user.email})
        else:
           self.abort(500)
        
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
        if d.user_id != "":
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
          'error': '%s' % str(e)
        }
        
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
    
""" Challenge Management """
class GetChallengesHandler(BaseRequestHandler):

  @check_hash
  def post(self):
    try:
        user_id = self.request.get('user_id')
        
        if not user_id:
           raise Exception('You need to specify the user_id') 
       
        data = []
        
        challenges = Challenge.gql("WHERE user_id_a = :1", user_id)
        if challenges.count() > 0:
            for c in challenges:
                user_adversary = User.get_by_id(int(c.user_id_b))
                if user_adversary:
                    c_object = c.toJson()
                    c_object['adversary'] = user_adversary.toJson()
                    data.append(c_object)
        
        challenges = Challenge.gql("WHERE turn > 0 AND user_id_b = :1", user_id)
        if challenges.count() > 0:        
            for c in challenges:
                user_adversary = User.get_by_id(int(c.user_id_a))
                if user_adversary:
                    c_object = c.toJson()
                    c_object['adversary'] = user_adversary.toJson()
                    data.append(c_object)
    except Exception as e:
        logging.exception(e)
        data = {
          'error': '%s' % str(e)
        }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))

class RequestChallengeHandler(BaseRequestHandler):
    
  @check_hash
  def post(self):
    try:
        userId = self.request.get('userId')
        registrationId = self.request.get('registrationId')
        facebookId = self.request.get('facebookId')
        
        data = {}
        
        user_a = User.get_by_id(int(userId))
        if not user_a:
            raise Exception('Nessun utente trovato')
        
        query1 = db.GqlQuery("SELECT * FROM User WHERE facebook_id = :1", str(facebookId))
        if query1.count() == 0:
            raise Exception('Nessun utente trovato')
        
        user_b = query1.get()
        if userId == str(user_b.key().id()):
            raise Exception('Non puoi sfidare te stesso')
        
        query2 = db.GqlQuery("SELECT * FROM Device WHERE user_id = :1", str(user_b.key().id()))
        if query2.count() == 0:
            raise Exception('Nessun device trovato')
        
        # Save challenge
        query = Challenge.gql("WHERE user_id_a = :1 and user_id_b = :2 and accepted = false and turn = 0", userId, '%s' % user_b.key().id())
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
          challenge = Challenge(user_id_a=userId, user_id_b='%s'%user_b.key().id(), registration_id_a=registrationId, accepted=False, turn=0)
          challenge.put()
          
        # Send request to all devices of user_b
        for device in query2.run():
            sendMessage(device.registration_id, collapse_key='request_challenge', payload={'user_id': challenge.user_id_b, 
                                                                                           'challenge_id': '%s'%challenge.key().id(), 
                                                                                           'adversary_facebook_id': user_a.facebook_id,
                                                                                           'adversary_name': user_a.first_name })
        
        data = {
          'message': 'ok'
        }
    except Exception as e:
        logging.exception(e)
        data = {
          'error': '%s' % str(e)
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
        
        print "registrationId: %s"%registrationId
        
        challenge.accepted = (accepted == "true")
        challenge.registration_id_b = registrationId
        challenge.turn = 1
        challenge.put()
        
        sendMessage(challenge.registration_id_a, payload={'user_id': challenge.user_id_a, 'appy_key': 'accepted_challenge_turn1_%s' % accepted, 'challenge_id': '%s'%challenge.key().id(), 'turn': '1'})
        
        data = {
          'message': 'ok'
        }
    except Exception as e:
        logging.exception(e)
        data = {
          'error': '%s' % str(e)
        }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
    
class QuestionsChallengeHandler(BaseRequestHandler):

  @check_hash
  def post(self):
    try:
        challengeId = self.request.get('challengeId')
        turn = self.request.get('turn')
        
        challenge = Challenge.get_by_id(int(challengeId))
        
        if not challenge:
            raise Exception('Nessuna sfida trovata')
        
        if challenge.turn != int(turn):
            raise Exception('C\'è stato un errore con la sfida')
        
        if turn == "1":
            import random
            
            questions = ChallengeQuestion.gql("WHERE category_id = 0")
            data = [questions.get().toJson()]
            
            categories = ChallengeQuestionCategory.all()
            for c in categories:
                questions = ChallengeQuestion.gql("WHERE category_id = :1", c.key().id())
                question = sample(random.random, questions, 1)
                data.append(question[0].toJson())
        else:
            answers = ChallengeAnswer.gql("WHERE challenge_id = :1", challengeId)
            questions = [ChallengeQuestion.get_by_id(int(answer.question_id)) for answer in answers]
            questions.sort(key=lambda x: x.order, reverse=False)
            data = [q.toJson() for q in questions]
    except Exception as e:
        logging.exception(e)
        data = {
          'error': '%s' % str(e)
        }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
        
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
        
        if turn == "1" and challenge.turn != 1:
            raise Exception('C\'è stato un errore con la sfida')
        
        if turn == "2" and challenge.turn != 2:
            raise Exception('C\'è stato un errore con la sfida')
        
        user_a = User.get_by_id(int(challenge.user_id_a))
        if not user_a:
            raise Exception('C\'è stato un errore con la sfida')
        
        user_b = User.get_by_id(int(challenge.user_id_b))
        if not user_b:
            raise Exception('C\'è stato un errore con la sfida')
        
        questions = json.loads(questions)
        score = 0
        
        for q in questions:
            question = ChallengeQuestion.get_by_id(int(q))
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
            data['tuFacebookId'] = user_b.facebook_id
            data['tuName'] = user_b.first_name
        else:
            challenge.score_b = float(score)
            challenge.turn = 3
            sendMessage(challenge.registration_id_a, payload={'user_id': challenge.user_id_a, 'appy_key': 'accepted_challenge_turn3', 
                                                              'ioChallenge': challenge.score_a, 'tuFacebookId': user_b.facebook_id, 
                                                              'tuName': user_b.first_name, 'tuChallenge': score, 'challenge_id': challenge.key().id(), 'turn': '3'})
            data['tuFacebookId'] = user_a.facebook_id
            data['tuName'] = user_a.first_name
            
        challenge.put()
    except Exception as e:
       logging.exception(e)
       data = {
         'error': '%s' % str(e)
       }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
    
class GetAppinessByDayHandler(BaseRequestHandler):

  @check_hash
  def post(self): 
    data = {}
    
    try:
        user_id = self.request.get('user_id')
        
        if not user_id:
           raise Exception('Devi specificare un user_id') 
        
        count = {}
        sum = {}
        
        firstOfMonth = mkFirstOfMonth(date.today())
        
        answers = Answer.gql("WHERE user_id = :1 AND date >= DATE(:2)", user_id, formatDate(firstOfMonth))
        
        for answer in answers:
            index = str(answer.date.date())
            if index in count:
                count[index] = count[index]+1
                sum[index] = sum[index]+int(answer.value)
            else:
                count[index] = 1
                sum[index] = int(answer.value)
                
        for index in count:
            data[index] = sum[index] / count[index]
    except Exception as e:
       logging.exception(e)
       data = {
         'error': '%s' % str(e)
       }
       
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))

class GetAppinessByMonthHandler(BaseRequestHandler):

  @check_hash
  def post(self): 
    data = {}
    
    try:
        user_id = self.request.get('user_id')
        
        if not user_id:
           raise Exception('Devi specificare un user_id') 
        
        count = {}
        sum = {}
        
        dtDateTime = date.today()
        dtDateTime = mkFirstOfMonth(dtDateTime)
        month = int(dtDateTime.strftime("%m"))
        dYear = dtDateTime.strftime("%Y")
        dDay = "1"  
        len_months = 5;
        
        for i in range(len_months):
            index = str(month-i-1)
            dMonth = str((month-i-1)%12)
            firstOfMonth = mkDateTime("%s-%s-%s"%(dYear,dMonth,dDay))
            lastOfMonth = mkLastOfMonth(firstOfMonth)
            
            answers = Answer.gql("WHERE user_id = :1 AND date >= DATE(:2) AND date <= DATE(:3)", user_id, formatDate(firstOfMonth), formatDate(lastOfMonth))
            
            if answers.count() > 0:
                count[index] = 0
                sum[index] = 0
                for answer in answers:
                    count[index] = count[index]+1
                    sum[index] = sum[index]+int(answer.value)
                if sum[index] == 0:
                    data[index] = 1
                else:    
                    data[index] = sum[index] / count[index]
    except Exception as e:
       logging.exception(e)
       data = {
         'error': '%s' % str(e)
       }
       
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
    
class CreateMap(BaseRequestHandler):

  def get(self):
      today = date.today()
      yesterday = today - timedelta(1)
      
      regions = Region.all()
      provincie = Provincia.all()
      answers = Answer.gql('WHERE date >= DATE(\'%s\') AND date < DATE(\'%s\') AND question_id = \'6434359225614336\''%(yesterday, today))
      
      if answers.count() == 0:
          raise Exception('No answers') 
      
      data = {}
      
      for region in regions:
        id = str(region.key().id())
        data[id] = {}
        data[id]['name'] = region.name
        data[id]['sum'] = 0
        data[id]['count'] = 0
        data[id]['type'] = 'R'
        data[id]['coordinate'] = region.coordinate
        
      for provincia in provincie:
        id = str(provincia.key().id())
        data[id] = {}
        data[id]['name'] = provincia.name
        data[id]['sum'] = 0
        data[id]['count'] = 0
        data[id]['type'] = 'P'
        data[id]['coordinate'] = provincia.coordinate
      
      for answer in answers:
          if answer.location:
            lat = answer.location.lat
            lng = answer.location.lon
            print "%s %s"%(lat, lng)
            
            for region in regions:
                id = str(region.key().id())
                geometry = json.loads(region.geometry)
                
                is_inside = False
                
                if 'geometries' in geometry:
                    is_inside = False
                    for g in geometry['geometries']:
                        if point_inside_polygon(lat, lng, g['coordinates'][0]):
                            is_inside = True
                            break
                    
                if 'geometry' in geometry:
                    is_inside = point_inside_polygon(lat, lng, geometry['geometry']['coordinates'][0])
                    
                if is_inside:
                    data[id]['sum'] = data[id]['sum'] + int(answer.value)
                    data[id]['count'] = data[id]['count'] + 1
            
            for provincia in provincie:
                id = str(provincia.key().id())
                geometry = json.loads(provincia.geometry)
                
                is_inside = False
                
                if 'geometries' in geometry:
                    is_inside = False
                    for g in geometry['geometries']:
                        if point_inside_polygon(lat, lng, g['coordinates'][0]):
                            is_inside = True
                            break
                    
                if 'geometry' in geometry:
                    is_inside = point_inside_polygon(lat, lng, geometry['geometry']['coordinates'][0])
                    
                if is_inside:
                    data[id]['sum'] = data[id]['sum'] + int(answer.value)
                    data[id]['count'] = data[id]['count'] + 1
    
      for id, object in data.iteritems():
          if object['count'] == 0:
              appyness = 1
          else:
              appyness = object['sum']/object['count']
          marker = Marker.gql('WHERE id = :1', id)
          marker = marker.get()
          
          if marker:
              marker.coordinate=object['coordinate']
              marker.appyness=appyness
              marker.name=object['name']
              marker.type=object['type']
              marker.put()
          else:
              marker = Marker(id=id, coordinate=object['coordinate'], appyness=appyness, name=object['name'], type=object['type'])
              marker.put()
      
      self.response.headers['Content-Type'] = 'application/json'
      self.response.out.write("ok")      

class GetDataMap(BaseRequestHandler):

  def get(self):
    data = []
    
    markers = Marker.all()
    for marker in markers:
        data.append({'name': marker.name, 'coordinate': str(marker.coordinate), 'appyness': marker.appyness, 'type': marker.type })
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
    
class LostPassword(BaseRequestHandler):

  @check_hash
  def post(self):
    try:
      email = self.request.get('email')
      
      if not email:
         raise Exception('Devi specificare una email')
     
      query = User.gql("WHERE email = :1", email)
      if query.count() == 0:
          raise Exception('Devi specificare una email utilizzata nel sistema')
      
      user = query.get()
     
      data = {'message': 'ok'}
    
      new_password = send_new_password(user.first_name, user.last_name, user.email, LOST_PASSWORD_EMAIL)
      user.password = new_password
      user.put()
    except Exception as e:
     logging.exception(e)
     data = {
       'error': '%s' % str(e)
     }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))

class ChangePassword(BaseRequestHandler):

  @check_hash
  def post(self):
    try:
      user_id = self.request.get('user_id')
      old_password = self.request.get('old_password')
      new_password = self.request.get('new_password')
      
      if not user_id:
         raise Exception('Devi specificare un user_id')
     
      user = User.get_by_id(int(user_id))
      if not user:
          raise Exception('Devi specificare un utente valido')
      
      if user.password != old_password:
          raise Exception('La vecchia password specificata è sbagliata')
     
      data = {'message': 'ok'}
    
      user.password = new_password
      user.put()
    except Exception as e:
     logging.exception(e)
     data = {
       'error': '%s' % str(e)
     }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
    
class UpdateFacebook(BaseRequestHandler):

  @check_hash
  def post(self):
    try:
      user_id = self.request.get('user_id')
      facebook_id = self.request.get('facebook_id')
      
      print "facebook_id: %s"%facebook_id
      
      if not user_id:
         raise Exception('Devi specificare un user_id')
     
      # if not facebook_id:
      #   raise Exception('Devi specificare un facebook_id')
     
      user = User.get_by_id(int(user_id))
      if not user:
          raise Exception('Devi specificare un utente valido')
      
      data = {'message': 'ok', 'facebook_id': facebook_id}
    
      user.facebook_id = facebook_id
      user.put()
    except Exception as e:
     logging.exception(e)
     data = {
       'error': '%s' % str(e)
     }
    
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))
    
class CrashReport(BaseRequestHandler):

  def post(self):
    arguments = self.request.arguments()
    arguments.sort()
    query_string = ""
    first = True
    
    for a in arguments:
        if not first:
            query_string = query_string + "&"
        
        query_string = query_string + a + "=" + self.request.get(a)
        first = False

    print "query_string: %s"%query_string
    data = {'message': 'ok'}
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))

"""
class PutProvincieMap(BaseRequestHandler):

  def get(self):
      access_token = getGoogleAccessToken()
      
      response = sqlPostFusionTable(access_token, "SELECT * FROM 1wBVVZ_IR4iXhvMQ0cZcAxblZYmH5Zs2Q7JSr3gM")
      response_json = json.loads(response)
      
      for i in xrange(len(response_json["rows"])):
          print "%s %s %s"%(response_json["rows"][i][1], 'geometry' in response_json["rows"][i][0], 'geometries' in response_json["rows"][i][0])
          region = Region(name=str(response_json["rows"][i][1]), geometry=db.Text(json.dumps(response_json["rows"][i][0])))
          region.put()
      
      response = sqlGetFusionTable(access_token, "SELECT * FROM 1vMejPOyqzvc80l_7Q21TEgYKWjzNdg07WASl3N8 WHERE Type = 'P'")
      response_json = json.loads(response)
      
      print "response_json: %s"%response_json
          
      for i in xrange(len(response_json["rows"])):
        print "name: %s"%response_json["rows"][i][1].encode('utf-8')
        provincia = Provincia(name=response_json["rows"][i][1].encode('utf-8'), geometry=db.Text(json.dumps(response_json["rows"][i][3])), coordinate=str(response_json["rows"][i][2]))
        provincia.put()
      
      self.response.headers['Content-Type'] = 'application/json'
      self.response.out.write("ok")
"""
