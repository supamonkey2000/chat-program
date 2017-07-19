package com.moore.joshua.minichat;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class MainActivity extends AppCompatActivity {

    public static TextView messageTV;
    public static EditText ipAddressET;
    public static EditText portET;
    public static EditText msgET;
    public static Button button;
    public static boolean didItWork;
    public String massage;
    public ObjectInputStream sInput;
    public ObjectOutputStream sOutput;
    public Socket socket;
    public String server, username;
    public int port;
    public String theMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipAddressET = (EditText) findViewById(R.id.ip);
        portET = (EditText) findViewById(R.id.port);
        messageTV = (TextView) findViewById(R.id.textView);
        msgET = (EditText) findViewById(R.id.messageEdit);
        button = (Button) findViewById(R.id.button);
        messageTV.setMovementMethod(new ScrollingMovementMethod());
        msgET.setMovementMethod(new ScrollingMovementMethod());
    }

    public void connect(View view) { // Attempt to connect to the server
        if (!didItWork) { // Check if the server is already connected. If not, try to connect
            username = msgET.getText().toString(); // Retrieve the username from the EditText
            System.out.println(ipAddressET.getText().toString() + " " + Integer.parseInt(portET.getText().toString())); // Debug
            messageTV.append("Going to try to connect..."); // Alert the user that the app is trying to connect
            server = ipAddressET.getText().toString(); // Retrieve the IP address from the EditText
            port = Integer.parseInt(portET.getText().toString()); // Retrieve the port from the EditText
            new ConnectTask().execute(); // Tell the networking class to start connecting
        } else { // If server is already connected then send a message
            System.out.println("Sending a message!"); // Debug
            massage = msgET.getText().toString(); // Retrieve the message from the EditText
            new SendTask().execute(); // Tell the other networking class to send the message
        }
    }

    public boolean foregrounded() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }

    public void notifications() { // Display a notification whenever a message is received (fixes required)
        if(!foregrounded()) {
            NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_stat_chatva).setContentTitle("Chatva Message").setContentText(theMessage);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification note = mBuilder.build();
            note.defaults |= Notification.DEFAULT_VIBRATE;
            note.defaults |= Notification.DEFAULT_SOUND;
            mNotificationManager.notify(1, note);
        }
    }

    private class ListenTask extends AsyncTask<Object, String, Object> { // This class waits for messages to be received
        @Override
        protected Object doInBackground(Object[] params) { // Do the task in the background
            System.out.println("Starting loop"); // Debug
            String msg3;
            while (true) { // Forever loop is safe here since it is Asynchronous is a separate thread
                try {
                    msg3 = (String) sInput.readObject(); // Convert the incoming data into a readable format
                    System.out.println(msg3); // Debug
                    theMessage = msg3;
                    publishProgress(msg3); // Send information (msg3) to other method
                } catch (IOException e) {
                    System.out.println("Server has closed the connection: " + e);
                    msg3 = "Error\n";
                    publishProgress(msg3);
                    break;
                } catch (Exception um) {
                    System.out.println("Error: " + um);
                }
            }
            return null; // We must have a return statement, but it must be null since the forever loop makes it inaccessible
        }

        @Override
        protected void onProgressUpdate(String[] text) { // This interacts with the UI and the rest of the program
            System.out.println(theMessage); // Debug
            String[] splitter = theMessage.split(":"); // Split the string by the : character
            int getcolor = MainActivity.messageTV.getCurrentTextColor(); // Get the current color from the TextView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Not sure why this is required but it crashes without it
                MainActivity.messageTV.setTextColor(getResources().getColor(R.color.blacky,null)); // Set color to what it was before -_-
            }
            MainActivity.messageTV.append(splitter[0] + ":"); // Append some info to  the TextView
            MainActivity.messageTV.setTextColor(getcolor); // Set the color again for some reason
            for(int i = 1; i < splitter.length; i++) { // Append everything from the info to the TextView
                MainActivity.messageTV.append(splitter[i] + ":"); // That comment should be here ^^^
            }
            notifications(); // Call a notification task
            System.out.println("updated and notified."); // Debug
        }

        @Override
        protected void onPostExecute(Object o) {
            System.out.println("It ended unfortunately"); // Something required but we don't ever use
        }
    }

    private class ConnectTask extends AsyncTask<Object, Object, Object> { // Fun networking class
        @Override
        protected Object doInBackground(Object[] params) { // Do the task in the background
            try {
                System.out.println("Socketing"); // Debug
                InetAddress address = InetAddress.getByName(server); // Get IP from variable
                String host = address.getHostName(); // Convert the address to a usable string for Sockets
                socket = new Socket(host, port); // Create the socket and connect to it
            } catch (Exception ec) { // Catch if connecting fails and print the error (its a sysout so its useless but whatever)
                System.out.println("Error connecting to server:" + ec); // Debug
                return false;
            }
            String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort(); // Accepted
            System.out.println(msg); // Debug
            try { // Just create In/Out streams, this 99.9% never fails
                sInput = new ObjectInputStream(socket.getInputStream());
                sOutput = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException eIO) { // Catch if it does fail somehow
                System.out.println("Exception creating new Input/output Streams: " + eIO);
                return false;
            }
            try {
                sOutput.writeObject(username); // Send the username to the server
                System.out.println(username); // Debug
                sOutput.flush(); // Clear out the OutputStream to be used for messages
            } catch (IOException eIO) { // SO MANY TRY/CATCHES!!
                System.out.println("Exception doing login : " + eIO);
                return false;
            }
            didItWork = true; // It. Worked.
            return true;
        }

        @SuppressLint("SetTextI18n") // Required. Not sure what it does
        @Override // I love how many Overrides are required for Android development
        protected void onPostExecute(Object o) { // When the background task is finished do this stuff
            if (didItWork) { // If it worked -_-
                MainActivity.messageTV.append(" Success\n"); // Alert the user they have successfully connected
                button.setText("Send"); // Change the connect button to say Send
                AsyncTaskCompat.executeParallel(new ListenTask()); // Allow for messages to received
            } else {
                MainActivity.messageTV.append(" Failure\n"); // Welp, we failed
            }
        }
    }

    private class SendTask extends AsyncTask<Object, Object, Object> { // The task that sends messages
        @Override
        protected Object doInBackground(Object[] params) { // Do the task in the background
            try {
                System.out.println("sending the damn thing\n"); // I got mad here cause it didn't work
                System.out.println(massage); // Debug
                if(massage.toLowerCase().contains("logout")) {
                    sOutput.writeObject("2:LOGOUT");
                }
                else if(massage.toLowerCase().contains("whoisin")) {
                    sOutput.writeObject("0:WHOISIN");
                }
                else {
                    sOutput.writeObject("1:" + massage); // Send the message (massage?)
                }
                System.out.println("flushing the damn thing\n"); // Still mad I guess
                sOutput.flush(); // Flush the OutputStream for more messages
                System.out.println("done"); // The first time I saw this I was very happy
            } catch (IOException ecxc) {
                System.out.println(ecxc.toString()); // Saw this error a lot... also  // Debug
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            msgET.setText("");
        } // Clear the message EditText
    }
}