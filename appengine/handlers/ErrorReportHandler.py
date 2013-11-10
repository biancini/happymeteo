'''
@author: Simon Vocella <voxsim@gmail.com>
'''
import json
import webapp2

from google.appengine.ext import db

from models import ErrorReport

class ErrorReportHandler(webapp2.RequestHandler):

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
            
        APP_VERSION_CODE = self.request.get("APP_VERSION_CODE")
        STACK_TRACE = self.request.get("STACK_TRACE")
        
        crashReport = ErrorReport(queryString=db.Text(query_string), code=APP_VERSION_CODE, stackTrace=STACK_TRACE)
        crashReport.put()
        data = {'message': 'ok'}
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(data))