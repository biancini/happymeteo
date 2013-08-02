from google.appengine.ext import db

class Users(db.Model):
    nome = db.StringProperty()
    cognome = db.StringProperty()
    genere = db.StringProperty()
    datadinascita = db.StringProperty()
    lavoro = db.StringProperty()
    citta = db.StringProperty()
