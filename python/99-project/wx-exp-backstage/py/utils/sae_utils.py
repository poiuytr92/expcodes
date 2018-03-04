import json
from sae import channel

def index(request):
    url = ''
    name = 'test_channel'
    duration = 10
    url = channel.create_channel(name, duration)
    if request.method == 'POST':
        content = request.POST.get('content', '')
        channel.send_message(name, str(content))
    return render_to_response('chat/index.html', {'url':url}, context_instance=RequestContext(request))

def connected(request):
    res = request.POST
    print res
    return HttpResponse(json.dumps(res, cls=ComplexEncoder), mimetype="text/plain")

def disconnected(${2:}):
    res = request.POST
    print res
    return HttpResponse(json.dumps(res, cls=ComplexEncoder), mimetype="text/plain")
    
def message(${2:}):
    res = request.POST
    print res
    return HttpResponse(json.dumps(res, cls=ComplexEncoder), mimetype="text/plain")
