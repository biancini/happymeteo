import webapp2
from views import MainPage, RegisterPage, UnregisterPage, SendMessagePage

app = webapp2.WSGIApplication([
        ('/', MainPage),
        ('/register', RegisterPage),
        ('/unregister', UnregisterPage),
        ('/send_message', SendMessagePage)
        ],
        debug=True)
        
"""
('/create', CreateNote), 
('/edit/([\d]+)', EditNote),
('/delete/([\d]+)', DeleteNote)
"""
