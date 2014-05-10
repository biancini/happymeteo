#!/bin/bash

cd appengine
../../google_appengine/appcfg.py update app.yaml backend-map.yaml backend-push.yaml
../../google_appengine/appcfg.py update_dispatch .
