# -*- coding: utf-8 -*-
from secrets import SESSION_KEY

from webapp2 import WSGIApplication, Route

# webapp2 config
app_config = {
  'webapp2_extras.sessions': {
    'cookie_name': '_simpleauth_sess',
    'secret_key': SESSION_KEY
  },
  'webapp2_extras.auth': {
    'user_attributes': []
  }
}
    
# Map URLs to handlers
routes = [
  Route('/', handler='handlers.RootHandler'),

  # profile user
  Route('/create_account', handler='handlers.CreateAccountHandler'),
  Route('/facebook_login', handler='handlers.FacebookLoginHandler'),
  Route('/normal_login', handler='handlers.NormalLoginHandler'),
  Route('/confirm_user', handler='handlers.ConfirmUserHandler'),

  # device management
  Route('/index_device', handler='handlers.IndexDeviceHandler'),
  Route('/register', handler='handlers.RegisterHandler'),
  Route('/unregister', handler='handlers.UnregisterHandler'),
  Route('/send_message', handler='handlers.SendMessageHandler'),

  # question management
  Route('/get_questions', handler='handlers.GetQuestionsHandler'),
  Route('/submit_questions', handler='handlers.SubmitQuestionsHandler'),
  
  # challenge management
  Route('/request_challenge', handler='handlers.RequestChallengeHandler'),
  Route('/accept_challenge', handler='handlers.AcceptChallengeHandler'),
  Route('/questions_challenge', handler='handlers.QuestionsChallengeHandler'),
  Route('/submit_challenge', handler='handlers.SubmitChallengeHandler'),
  #Route('/send_score_challenge', handler='handlers.SendScoreChallengeHandler')
  
  # happy management
  Route('/happy_meteo', handler='handlers.HappyMeteoHandler'),
  Route('/happy_context', handler='handlers.HappyContextHandler'),
]

app = WSGIApplication(routes, config=app_config, debug=True)
