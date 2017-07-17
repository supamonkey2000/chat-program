package com.moore.joshua.minichat;
import android.annotation.SuppressLint;
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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    public static TextView messageTV;
    public static EditText ipAddressET;
    public static EditText portET;
    public static EditText msgET;
    public static Button button;
    public static boolean didItWork;
    public String massage;
    public DataInputStream sInput;
    public DataOutputStream sOutput;
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

    public void connect(View view) {
        if (!didItWork) {
            username = msgET.getText().toString();
            System.out.println(ipAddressET.getText().toString() + " " + Integer.parseInt(portET.getText().toString()));
            messageTV.append("Going to try to connect...");
            server = ipAddressET.getText().toString();
            port = Integer.parseInt(portET.getText().toString());
            new ConnectTask().execute();
        } else {
            System.out.println("Sending a message!");
            massage = msgET.getText().toString();
            new SendTask().execute();
        }
    }

    public void notifications() {
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setSmallIcon(R.drawable.common_google_signin_btn_icon_dark).setContentTitle("MiniChat Message").setContentText(theMessage);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1,mBuilder.build());
    }

    private class ListenTask extends AsyncTask<Object, String, Object> {
        @Override
        protected Object doInBackground(Object[] params) {
            System.out.println("Starting loop");
            String msg3;
            while (true) {
                try {
                    msg3 = sInput.readUTF();
                    System.out.println(msg3);
                    theMessage = msg3;
                    publishProgress(msg3);
                } catch (IOException e) {
                    System.out.println("Server has close the connection: " + e);
                    msg3 = "Error\n";
                    publishProgress(msg3);
                    break;
                } catch (Exception um) {
                    System.out.println("Error: " + um);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String[] text) {
            System.out.println(theMessage);
            String[] splitter = theMessage.split(":");
            int getcolor = MainActivity.messageTV.getCurrentTextColor();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                MainActivity.messageTV.setTextColor(getResources().getColor(R.color.blacky,null));
            }
            MainActivity.messageTV.append(splitter[0] + ":");
            MainActivity.messageTV.setTextColor(getcolor);
            for(int i = 1; i < splitter.length; i++) {
                MainActivity.messageTV.append(splitter[i]);
            }
            notifications();
            System.out.println("updated and notified.");
        }

        @Override
        protected void onPostExecute(Object o) {
            System.out.println("It ended unfortunately");
        }
    }

    private class ConnectTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                System.out.println("Socketing");
                InetAddress address = InetAddress.getByName(server);
                String host = address.getHostName();
                socket = new Socket(host, port);
            } catch (Exception ec) {
                System.out.println("Error connecting to server:" + ec);
                return false;
            }
            String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
            System.out.println(msg);
            try {
                sInput = new DataInputStream(socket.getInputStream());
                sOutput = new DataOutputStream(socket.getOutputStream());
            } catch (IOException eIO) {
                System.out.println("Exception creating new Input/output Streams: " + eIO);
                return false;
            }
            try {
                sOutput.writeUTF(username);
                sOutput.flush();
            } catch (IOException eIO) {
                System.out.println("Exception doing login : " + eIO);
                return false;
            }
            didItWork = true;
            return true;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Object o) {
            if (didItWork) {
                MainActivity.messageTV.append(" Success\n");
                button.setText("Send");
                AsyncTaskCompat.executeParallel(new ListenTask());
            } else {
                MainActivity.messageTV.append(" Failure\n");
            }
        }
    }

    private class SendTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                System.out.println("sending the damn utf\n");
                sOutput.writeUTF(massage);
                System.out.println("flushing the damn thing\n");
                sOutput.flush();
                System.out.println("done");
            } catch (IOException ecxc) {
                System.out.println(ecxc.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            msgET.setText("");
        }
    }
}