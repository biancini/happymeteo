# -*- coding: utf-8 -*-
from webapp2 import WSGIApplication, Route

from handlers import CreateAccountHandler, FacebookLoginHandler, \
    NormalLoginHandler, ConfirmUserHandler, RegisterHandler, UnregisterHandler, \
    SendQuestionsHandler, GetQuestionsHandler, SubmitQuestionsHandler, \
    GetChallengesHandler, RequestChallengeHandler, AcceptChallengeHandler, \
    QuestionsChallengeHandler, SubmitChallengeHandler, GetAppinessByDayHandler, \
    GetAppinessByMonthHandler, CreateMapHandler, GetDataMapHandler, \
    LostPasswordHandler, ChangePasswordHandler, UpdateFacebookHandler, \
    CrashReportHandler
    
from secrets import SESSION_KEY

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
  Route('/create_account', handler=CreateAccountHandler),
  Route('/facebook_login', handler=FacebookLoginHandler),
  Route('/normal_login', handler=NormalLoginHandler),
  Route('/confirm_user', handler=ConfirmUserHandler),

  # device management
  Route('/register', handler=RegisterHandler),
  Route('/unregister', handler=UnregisterHandler),
  Route('/send_questions', handler=SendQuestionsHandler),

  # question management
  Route('/get_questions', handler=GetQuestionsHandler),
  Route('/submit_questions', handler=SubmitQuestionsHandler),
  
  # challenge management
  Route('/get_challenges', handler=GetChallengesHandler),
  Route('/request_challenge', handler=RequestChallengeHandler),
  Route('/accept_challenge', handler=AcceptChallengeHandler),
  Route('/questions_challenge', handler=QuestionsChallengeHandler),
  Route('/submit_challenge', handler=SubmitChallengeHandler),
  
  Route('/get_appiness_by_day', handler=GetAppinessByDayHandler),
  Route('/get_appiness_by_month', handler=GetAppinessByMonthHandler),
  
  Route('/create_map', handler=CreateMapHandler),
  Route('/get_data_map', handler=GetDataMapHandler),
  
  Route('/lost_password', handler=LostPasswordHandler),
  Route('/change_password', handler=ChangePasswordHandler),
  Route('/update_facebook', handler=UpdateFacebookHandler),
  
  Route('/crash_report', handler=CrashReportHandler)
]

app = WSGIApplication(routes, config=app_config, debug=True)
