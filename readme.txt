Author: Tim Lindquist (Tim.Lindquist@asu.edu), ASU Polytechnic, CIDSE, SE
Version: July 2016

See http://pooh.poly.asu.edu/Mobile
See http://pooh.poly.asu.edu/Ser321

Purpose: demonstrate Json-RPC with TCP/IP Java server and manually created server
skeleton and client proxy.
The server and terminal client are executable on both Mac OS X and Debian Linux.

Communication between the service is done using JSON-RPC. Communication between
the client and server is accomplished using TCP/IP sockets in which the protocol
is for the client to send a valid jsonrpc request for one of the methods implemented
by the server. The server reads from the sockets input stream, unmarshals the request,
calls the appropriate method, marshals the result (boolean, Student, String, or String[])
and sends the jsonrpc response back to the client via it output stream.
The purpose of the example is to demonstrate JSON and JSON-RPC via direct TCP/IP sockets.
Other examples in the course demonstrate using frameworks to implement jsonrpc clients and
servers where communication occurs via http.
Use the following sources for background on these technologies:

JSON (JavaScript Object Notation):
 http://en.wikipedia.org/wiki/JSON
 The JSON web site: http://json.org/

JSON-RPC (JSON Remote Procedure Call):
 http://www.jsonrpc.org
 http://en.wikipedia.org/wiki/JSON-RPC

Any text on socket programming with Java.

Building and running the server and terminal clients is done with Ant.
This example depends on the following frameworks:
1. Ant
   see: http://ant.apache.org/
2. Json for the jdk as implemented by Doug Crockford.
   See: https://github.com/stleary/JSON-java
3. JsonRPC framework for Java and Swift.


To build and run the examples, you will need to have Ant installed on
your system.

ant build.all (or ant build.server followed by ant build.client)
execute the java server from the command line with:
tcp server: java -cp classes:lib/json.jar ser321.tcpjsonrpc.server.StudentCollectionTCPJsonRPCServer 8080

execute the command line java client with the command:
java -cp classes:lib/json.jar ser321.tcpjsonrpc.StudentCollectionClient 127.0.0.1 8080

To clean the project directory:
ant clean

end

