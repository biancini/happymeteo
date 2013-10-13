# -*- coding: utf-8 -*-
from secrets import SESSION_KEY

from webapp2 import WSGIApplication, Route

#import fix_path

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
  # profile user
  Route('/create_account', handler='handlers.CreateAccountHandler'),
  Route('/facebook_login', handler='handlers.FacebookLoginHandler'),
  Route('/normal_login', handler='handlers.NormalLoginHandler'),
  Route('/confirm_user', handler='handlers.ConfirmUserHandler'),

  # device management
  Route('/register', handler='handlers.RegisterHandler'),
  Route('/unregister', handler='handlers.UnregisterHandler'),
  Route('/send_message', handler='handlers.SendMessageHandler'),

  # question management
  Route('/get_questions', handler='handlers.GetQuestionsHandler'),
  Route('/submit_questions', handler='handlers.SubmitQuestionsHandler'),
  
  # challenge management
  Route('/get_challenges', handler='handlers.GetChallengesHandler'),
  Route('/request_challenge', handler='handlers.RequestChallengeHandler'),
  Route('/accept_challenge', handler='handlers.AcceptChallengeHandler'),
  Route('/questions_challenge', handler='handlers.QuestionsChallengeHandler'),
  Route('/submit_challenge', handler='handlers.SubmitChallengeHandler'),
  
  Route('/get_appiness_by_day', handler='handlers.GetAppinessByDayHandler'),
  Route('/create_map', handler='handlers.CreateMap'),
  Route('/get_data_map', handler='handlers.GetDataMap'),
  Route('/lost_password', handler='handlers.LostPassword'),
  Route('/change_password', handler='handlers.ChangePassword'),
  Route('/update_facebook', handler='handlers.UpdateFacebook'),
  Route('/crash_report', handler='handlers.CrashReport')
  #Route('/put_provincie_map', handler='handlers.PutProvincieMap')
]

app = WSGIApplication(routes, config=app_config, debug=True)
