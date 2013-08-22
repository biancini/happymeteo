# -*- coding: utf-8 -*-
import sys
from secrets import SESSION_KEY

from webapp2 import WSGIApplication, Route

# inject './lib' dir in the path so that we can simply do "import ndb" 
# or whatever there's in the app lib dir.
if 'lib' not in sys.path:
    sys.path[0:0] = ['lib']

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

  # device managment
  Route('/index_device', handler='handlers.IndexDeviceHandler'),
  Route('/register', handler='handlers.RegisterHandler'),
  Route('/unregister', handler='handlers.UnregisterHandler'),
  Route('/send_message', handler='handlers.SendMessageHandler'),

  # question managment
  Route('/get_questions', handler='handlers.GetQuestionsHandler'),
  #Route('/submit_questions', handler='handlers.SubmitQuestionsHandler'),
]

app = WSGIApplication(routes, config=app_config, debug=True)
