bind = "0.0.0.0:8080"
workers = 4
worker_class = "uvicorn.workers.UvicornWorker"
timeout = 60










"""Gunicorn configuration for serving the FastAPI application.

All settings below are commented out to disable this configuration.
Uncomment only the lines you intend to use.
"""

import multiprocessing
import os


# bind = os.getenv("GUNICORN_BIND", "0.0.0.0:8000")
# worker_class = "uvicorn.workers.UvicornWorker"
# workers = int(os.getenv("WEB_CONCURRENCY", str(max(1, multiprocessing.cpu_count() // 2))))
# timeout = int(os.getenv("GUNICORN_TIMEOUT", "60"))
# graceful_timeout = int(os.getenv("GUNICORN_GRACEFUL_TIMEOUT", "30"))
# keepalive = int(os.getenv("GUNICORN_KEEPALIVE", "5"))
# loglevel = os.getenv("GUNICORN_LOG_LEVEL", "info")
# accesslog = os.getenv("GUNICORN_ACCESSLOG", "-")
# errorlog = os.getenv("GUNICORN_ERRORLOG", "-")
# preload_app = True
