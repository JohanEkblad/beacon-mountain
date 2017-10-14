#!/usr/bin/env python
#
# Yes, it's also possible to start a YAPP server somewhere on the Internet,
# if you don't want to run the server on one of your phones
#
# Currently the server only keep the data in an associative array, everything
# will be lost when you restart the server

import socket
import sys

peoples={}

def sendReply(connection, clientString):
  "Send a reply from the client request"
  parts = clientString.split(':')
  if len(parts) != 5:
    print >> sys.stdout, 'YAPP error: Client message must consists of 5 fields'
    return
  if parts[0] != "HELO":
    print >> sys.stdout, 'YAPP error: No HELO'
    return
  nickname=parts[1]
  latitude=parts[2]
  longitude=parts[3]
  answer=parts[4]
 
  peoples[nickname]=(nickname,latitude,longitude)

  reply=''
  if (answer == 'N'):
    reply="YOLO"
  elif (answer == 'Y'):
    reply="DATA:"+str(len(peoples.keys()))
    for people in peoples.keys():
      data = peoples[people]
      nickname=data[0]
      latitude=data[1]
      longitude=data[2]
      reply=reply+':'+nickname+':'+str(latitude)+':'+str(longitude)
  else:
    print >> sys.stdout, 'YAPP error: answer must be "Y" or "N"'

  reply=reply+'\0'
  connection.sendall(reply)

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_address = ('', 4711)
print >>sys.stdout, 'Starting up on %s port %s' % server_address
sock.bind(server_address)
sock.listen(5) # Queue up to 5 requests
while True:
  print >>sys.stdout, 'Waiting for a client connection'
  connection, client_address = sock.accept()
  try:
    print >>sys.stdout, 'Connection from', client_address
    clientString=''
    while True:
      data = connection.recv(1)
      if data:
        if data == '\0':
          sendReply(connection,clientString)
          break;
        else:
          clientString=clientString+data
      else:
        break;
  finally:
    connection.close()
