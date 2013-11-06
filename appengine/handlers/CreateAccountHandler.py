'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import logging

from handlers import BaseRequestHandler, check_hash
from models import User
from secrets import EMAIL, CREATE_ACCOUNT_EMAIL, CHANGE_FACEBOOK_EMAIL
from utils import happymeteo, send_new_password

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