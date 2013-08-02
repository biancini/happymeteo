import webapp2
from views import MainPage
"""
from views import MainPage, CreateNote, DeleteNote, EditNote
"""
app = webapp2.WSGIApplication([
        ('/', MainPage)
        ],
        debug=True)
        
"""
,
('/create', CreateNote), 
('/edit/([\d]+)', EditNote),
('/delete/([\d]+)', DeleteNote)
"""
