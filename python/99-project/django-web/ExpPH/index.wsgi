import sae
from ExpPH import wsgi

# def app(environ, start_response):
#     status = '200 OK'
#     response_headers = [('Content-type', 'text/html; charset=utf-8')]
#     start_response(status, response_headers)
#     return ['<strong>Welcome to Exp\'s Personal Home Page!</strong>']
    
application = sae.create_wsgi_app(wsgi.application)

# import django.core.handlers.wsgi
# application = sae.create_wsgi_app(django.core.handlers.wsgi.WSGIHandler())
