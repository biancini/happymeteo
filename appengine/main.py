# -*- coding: utf-8 -*-
from webapp2 import WSGIApplication, Route

from handlers import CreateAccountHandler, FacebookLoginHandler, \
    NormalLoginHandler, ConfirmUserHandler, RegisterHandler, UnregisterHandler, \
    SendQuestionsHandler, GetQuestionsHandler, SubmitQuestionsHandler, \
    GetChallengesHandler, RequestChallengeHandler, AcceptChallengeHandler, \
    QuestionsChallengeHandler, SubmitChallengeHandler, \
    GetAppinessByDayHandler, CreateMapHandler, GetDataMapHandler, \
    LostPasswordHandler, ChangePasswordHandler, UpdateFacebookHandler, \
    CSVUserHandler, CSVAnswerHandler, CSVChallengeAnswerHandler,\
    GetAppinessByWeekHandler, ErrorReportHandler
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
  Route('/create_account', handler=CreateAccountHandler.CreateAccountHandler),
  Route('/facebook_login', handler=FacebookLoginHandler.FacebookLoginHandler),
  Route('/normal_login', handler=NormalLoginHandler.NormalLoginHandler),
  Route('/confirm_user', handler=ConfirmUserHandler.ConfirmUserHandler),

  # device management
  Route('/register', handler=RegisterHandler.RegisterHandler),
  Route('/unregister', handler=UnregisterHandler.UnregisterHandler),
  Route('/send_questions', handler=SendQuestionsHandler.SendQuestionsHandler),

  # question management
  Route('/get_questions', handler=GetQuestionsHandler.GetQuestionsHandler),
  Route('/submit_questions', handler=SubmitQuestionsHandler.SubmitQuestionsHandler),
  
  # challenge management
  Route('/get_challenges', handler=GetChallengesHandler.GetChallengesHandler),
  Route('/request_challenge', handler=RequestChallengeHandler.RequestChallengeHandler),
  Route('/accept_challenge', handler=AcceptChallengeHandler.AcceptChallengeHandler),
  Route('/questions_challenge', handler=QuestionsChallengeHandler.QuestionsChallengeHandler),
  Route('/submit_challenge', handler=SubmitChallengeHandler.SubmitChallengeHandler),
  
  # appiness management
  Route('/get_appiness_by_day', handler=GetAppinessByDayHandler.GetAppinessByDayHandler),
  Route('/get_appiness_by_week', handler=GetAppinessByWeekHandler.GetAppinessByWeekHandler),
  Route('/create_map', handler=CreateMapHandler.CreateMapHandler),
  Route('/get_data_map', handler=GetDataMapHandler.GetDataMapHandler),
  
  # settings management
  Route('/lost_password', handler=LostPasswordHandler.LostPasswordHandler),
  Route('/change_password', handler=ChangePasswordHandler.ChangePasswordHandler),
  Route('/update_facebook', handler=UpdateFacebookHandler.UpdateFacebookHandler),
  
  # crash management
  Route('/crash_report', handler=ErrorReportHandler.ErrorReportHandler),
  
  # csv management
  Route('/csv_user', handler=CSVUserHandler.CSVUserHandler),
  Route('/csv_answer', handler=CSVAnswerHandler.CSVAnswerHandler),
  Route('/csv_challenge_answer', handler=CSVChallengeAnswerHandler.CSVChallengeAnswerHandler)
]

app = WSGIApplication(routes, config=app_config, debug=True)
