"""WSGI entrypoint used by process managers such as Gunicorn."""

from app.main import app 


# Gunicorn looks for an ``application`` variable when using the WSGI protocol.
application = app