# chat-program (Chatva)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9cfa02d88a3d40f2969fe0d1b009a853)](https://www.codacy.com/app/joshuamoore235/chat-program?utm_source=github.com&utm_medium=referral&utm_content=supamonkey2000/chat-program&utm_campaign=badger)

Java based client-server chat program for desktop and Android\*.

**Chat-program** is a simple chat program. It allows instant, lightweight text
communication between any device running Java (this includes the Android application).

**How to use:**
1. Start the Server on a desktop. This can be Windows, Linux, or Mac OS X.
2. Select a port to use (port forward ONLY IF NEEDED), and click start.
3. Open the client application. Enter the IP address and port of the server. Enter a username, then connect.
4. Enter messages and hit send to chat.

To use with a command line or terminal, put `help` after the name of the jar 
file, like `java -jar chatva-d_v_x.x.x.jar help`.

The program can run on ARM, 32-bit, and 64-bit systems running Java.

**Future plans:**
1. Improve design of Android app
2. Fix restarting server without closing
3. Implement remote server management
4. Detect if client has been inactive for too long, then request a kick

\* *There are currently NO plans to develop an iOS app. If an iOS developer is interested in developing one based around my server, feel free to contact me via a Pull request.*
