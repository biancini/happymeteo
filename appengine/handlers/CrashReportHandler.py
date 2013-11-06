import json

from google.appengine.ext import db

from handlers import BaseRequestHandler
from models import ErrorReport

class CrashReportHandler(BaseRequestHandler):

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

    crashReport = ErrorReport(queryString=db.Text(query_string))
    crashReport.put()
    data = {'message': 'ok'}
    self.response.headers['Content-Type'] = 'application/json'
    self.response.out.write(json.dumps(data))