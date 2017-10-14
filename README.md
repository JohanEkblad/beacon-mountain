# beacon-mountain
P2P position app. This is an app that can act both as a server and as a client. All clients send their nickname and position to the server. The server keep track on all connected clients and return their positions when a client post a new location. 

##Overview of the communication

![overview](overview.jpg)

1. An app is started in server mode
2. One or more apps is started in client mode. When the client starts it can choose to input the servers IP directly or leave blank for receiving an SMS with servers IP from the server
3. (optionally) The server can send one/more SMS:es to the clients (currently using the servers phone's address book)
4. (optionally) The client receives the SMS (with the servers IP) from the server

The connection between the client(s) and the server is now established. All clients initiates the connection to the server.

5. The client send their nickname, position and a flag telling the server if the client wish the receive all other clients positions or not (a client unit attached on a dog, for example, might not need all others positions)
6. The server replies with nothing (if the flag is 'N') or the nicknames and postions of all other clients (and the server)

##Possible enhancements

* Save position data more permanent (in a database) on the server
* Let the server remember the nicknames in a group
* Let the server give the group a name
* Control the interval, how often the clients send thier positions to the server. (A dog might for example send their positions more often than other clients)

